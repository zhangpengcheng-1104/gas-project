<template>
    <el-dialog
        title="选择地址"
        v-model="visible"
        width="1000px"
        :close-on-click-modal="false"
        @open="initMap"
        @closed="destroyMap"
    >
        <div class="map-wrapper">
            <div class="map-container">
                <div class="search-box">
                    <el-input
                        v-model="searchKeyword"
                        placeholder="搜索地址"
                        clearable
                        @input="onSearchInput"
                        @keyup.enter="searchPoi"
                    >
                        <template #append>
                            <el-button icon="el-icon-search" @click="searchPoi">搜索</el-button>
                        </template>
                    </el-input>
                    <div v-if="searchResults.length > 0" class="search-results">
                        <div
                            v-for="item in searchResults"
                            :key="item.id"
                            class="search-item"
                            @click="selectSearchPoi(item)"
                        >
                            <div class="name">{{ item.name }}</div>
                            <div class="address">{{ item.address || item.district }}</div>
                        </div>
                    </div>
                </div>
                <div id="map-container" class="map"></div>
                <div class="current-address" v-if="currentAddress">
                    <span>当前选中：{{ currentAddress }}</span>
                </div>
            </div>
            <div class="poi-panel">
                <div class="poi-header">
                    <span class="poi-title">附近地点</span>
                    <span class="poi-tip" v-if="currentLocation">（点击地图可搜索附近地点）</span>
                </div>
                <div class="poi-list" v-loading="poiLoading">
                    <div v-if="nearbyPois.length === 0 && !poiLoading" class="no-poi">
                        <span v-if="!currentLocation">请先点击地图选择位置</span>
                        <span v-else>附近暂无地点信息</span>
                    </div>
                    <div
                        v-for="(poi, index) in nearbyPois"
                        :key="poi.id || index"
                        class="poi-item"
                        :class="{ active: selectedPoi && selectedPoi.id === poi.id }"
                        @click="selectNearbyPoi(poi)"
                    >
                        <div class="poi-index">{{ index + 1 }}</div>
                        <div class="poi-content">
                            <div class="poi-name">{{ poi.name }}</div>
                            <div class="poi-address">{{ poi.address || poi.pname + poi.cityname + poi.adname }}</div>
                            <div class="poi-distance" v-if="poi.distance">距离：{{ formatDistance(poi.distance) }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="visible = false">取消</el-button>
                <el-button type="primary" @click="confirmSelect">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
export default {
    name: 'AmapSelector',
    data() {
        return {
            visible: false,
            map: null,
            marker: null,
            geocoder: null,
            placeSearch: null,
            searchKeyword: '',
            searchResults: [],
            nearbyPois: [],
            poiLoading: false,
            currentAddress: '',
            currentLocation: null,
            selectedPoi: null,
            amapConfig: null,
            searchTimer: null,
            areaName: ''
        };
    },
    methods: {
        init(address, areaName) {
            this.visible = true;
            this.currentAddress = address || '';
            this.searchKeyword = address || '';
            this.searchResults = [];
            this.nearbyPois = [];
            this.currentLocation = null;
            this.selectedPoi = null;
            this.areaName = areaName || '';
        },
        async initMap() {
            try {
                const config = await this.loadAmapConfig();
                this.amapConfig = config;
                await this.loadAmapScript(config);
                this.createMap();
            } catch (error) {
                this.$message.error('地图加载失败：' + error.message);
            }
        },
        loadAmapConfig() {
            return new Promise((resolve, reject) => {
                this.$http('/amap/config', 'GET', {}, true, (resp) => {
                    if (resp && (resp.code === 0 || resp.code === 200)) {
                        resolve(resp.result);
                    } else {
                        reject(new Error('获取地图配置失败'));
                    }
                });
            });
        },
        loadAmapScript(config) {
            return new Promise((resolve, reject) => {
                if (window.AMap) {
                    resolve();
                    return;
                }
                
                window._AMapSecurityConfig = {
                    securityJsCode: config.securityJsCode
                };
                
                const script = document.createElement('script');
                script.type = 'text/javascript';
                script.src = `https://webapi.amap.com/maps?v=2.0&key=${config.webKey}&callback=onAMapLoaded`;
                script.onerror = () => reject(new Error('地图脚本加载失败'));
                
                window.onAMapLoaded = () => {
                    resolve();
                };
                
                document.head.appendChild(script);
            });
        },
        createMap() {
            const AMap = window.AMap;
            
            this.map = new AMap.Map('map-container', {
                zoom: 13,
                center: [106.5516, 29.5630],
                viewMode: '2D'
            });
            
            AMap.plugin(['AMap.Geocoder', 'AMap.PlaceSearch', 'AMap.AutoComplete'], () => {
                this.geocoder = new AMap.Geocoder({
                    city: '重庆'
                });
                this.placeSearch = new AMap.PlaceSearch({
                    city: '重庆',
                    pageSize: 20,
                    pageIndex: 1,
                    extensions: 'all'
                });
            });
            
            this.map.on('click', (e) => {
                this.onMapClick(e.lnglat);
            });
            
            if (this.areaName) {
                this.locateByAreaName(this.areaName);
            } else if (this.currentAddress) {
                this.searchPoi();
            }
        },
        locateByAreaName(areaName) {
            if (!this.geocoder) return;
            
            const fullAreaName = areaName.includes('重庆') ? areaName : '重庆市' + areaName;
            
            this.geocoder.getLocation(fullAreaName, (status, result) => {
                if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
                    const geocode = result.geocodes[0];
                    let lng, lat;
                    
                    if (geocode.location && typeof geocode.location === 'object') {
                        lng = geocode.location.lng;
                        lat = geocode.location.lat;
                    } else if (geocode.location && typeof geocode.location === 'string') {
                        const parts = geocode.location.split(',');
                        lng = parseFloat(parts[0]);
                        lat = parseFloat(parts[1]);
                    }
                    
                    if (!lng || !lat) {
                        console.warn('无法获取行政区域坐标');
                        return;
                    }
                    
                    this.map.setCenter([lng, lat]);
                    this.map.setZoom(14);
                    
                    this.currentLocation = { lng, lat };
                    
                    if (!this.marker) {
                        const AMap = window.AMap;
                        this.marker = new AMap.Marker({
                            position: [lng, lat]
                        });
                        this.map.add(this.marker);
                    } else {
                        this.marker.setPosition([lng, lat]);
                    }
                    
                    this.searchNearbyPoi(lng, lat);
                }
            });
        },
        destroyMap() {
            if (this.map) {
                this.map.destroy();
                this.map = null;
            }
            this.marker = null;
            this.geocoder = null;
            this.placeSearch = null;
        },
        onMapClick(lnglat) {
            this.currentLocation = {
                lng: lnglat.lng,
                lat: lnglat.lat
            };
            this.selectedPoi = null;
            
            if (!this.marker) {
                const AMap = window.AMap;
                this.marker = new AMap.Marker({
                    position: [lnglat.lng, lnglat.lat]
                });
                this.map.add(this.marker);
            } else {
                this.marker.setPosition([lnglat.lng, lnglat.lat]);
            }
            
            this.getAddressByLngLat(lnglat);
            this.searchNearbyPoi(lnglat.lng, lnglat.lat);
        },
        getAddressByLngLat(lnglat) {
            if (!this.geocoder) return;
            
            this.geocoder.getAddress([lnglat.lng, lnglat.lat], (status, result) => {
                if (status === 'complete' && result.regeocode) {
                    const formattedAddress = result.regeocode.formattedAddress;
                    this.currentAddress = formattedAddress;
                    this.searchKeyword = formattedAddress;
                }
            });
        },
        searchNearbyPoi(lng, lat) {
            if (lng === undefined || lat === undefined || lng === null || lat === null) {
                console.warn('searchNearbyPoi: 无效的坐标参数', lng, lat);
                return;
            }
            
            this.poiLoading = true;
            this.nearbyPois = [];
            
            this.$http('/amap/searchNearby', 'GET', {
                location: `${lng},${lat}`,
                radius: 1000,
                offset: 20,
                page: 1
            }, true, (resp) => {
                this.poiLoading = false;
                console.log('searchNearby response:', resp);
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    const result = resp.result;
                    console.log('result:', result);
                    if (result) {
                        if (result.status === '1' && result.pois && result.pois.length > 0) {
                            this.nearbyPois = result.pois;
                        } else if (result.pois && result.pois.length > 0) {
                            this.nearbyPois = result.pois;
                        } else {
                            console.log('无POI数据或状态异常:', result.status, result.info);
                        }
                    }
                }
            }).catch((err) => {
                this.poiLoading = false;
                console.error('searchNearby error:', err);
            });
        },
        onSearchInput() {
            if (this.searchTimer) {
                clearTimeout(this.searchTimer);
            }
            this.searchTimer = setTimeout(() => {
                this.searchPoi();
            }, 300);
        },
        searchPoi() {
            if (!this.searchKeyword) {
                this.searchResults = [];
                return;
            }
            
            const params = {
                keywords: this.searchKeyword,
                city: '重庆',
                cityLimit: 'true',
                offset: 10,
                page: 1
            };
            
            this.$http('/amap/searchPoi', 'GET', params, true, (resp) => {
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    const result = resp.result;
                    if (result.status === '1' && result.pois) {
                        this.searchResults = result.pois;
                    } else {
                        this.searchResults = [];
                    }
                }
            });
        },
        selectSearchPoi(poi) {
            this.currentAddress = poi.address || poi.pname + poi.cityname + poi.adname + poi.name;
            this.searchKeyword = this.currentAddress;
            this.searchResults = [];
            this.selectedPoi = poi;
            
            if (poi.location) {
                const lnglat = poi.location.split(',');
                this.currentLocation = {
                    lng: parseFloat(lnglat[0]),
                    lat: parseFloat(lnglat[1])
                };
                
                this.map.setCenter([this.currentLocation.lng, this.currentLocation.lat]);
                this.map.setZoom(16);
                
                if (!this.marker) {
                    const AMap = window.AMap;
                    this.marker = new AMap.Marker({
                        position: [this.currentLocation.lng, this.currentLocation.lat]
                    });
                    this.map.add(this.marker);
                } else {
                    this.marker.setPosition([this.currentLocation.lng, this.currentLocation.lat]);
                }
                
                this.searchNearbyPoi(this.currentLocation.lng, this.currentLocation.lat);
            }
        },
        selectNearbyPoi(poi) {
            this.selectedPoi = poi;
            this.currentAddress = poi.address || poi.pname + poi.cityname + poi.adname + poi.name;
            this.searchKeyword = this.currentAddress;
            
            if (poi.location) {
                const lnglat = poi.location.split(',');
                this.currentLocation = {
                    lng: parseFloat(lnglat[0]),
                    lat: parseFloat(lnglat[1])
                };
                
                this.map.setCenter([this.currentLocation.lng, this.currentLocation.lat]);
                
                if (!this.marker) {
                    const AMap = window.AMap;
                    this.marker = new AMap.Marker({
                        position: [this.currentLocation.lng, this.currentLocation.lat]
                    });
                    this.map.add(this.marker);
                } else {
                    this.marker.setPosition([this.currentLocation.lng, this.currentLocation.lat]);
                }
            }
        },
        formatDistance(distance) {
            const d = parseInt(distance);
            if (d < 1000) {
                return d + '米';
            } else {
                return (d / 1000).toFixed(1) + '公里';
            }
        },
        confirmSelect() {
            if (!this.currentAddress) {
                this.$message.warning('请选择一个地址');
                return;
            }
            
            this.$emit('select', {
                address: this.currentAddress,
                location: this.currentLocation,
                poi: this.selectedPoi
            });
            this.visible = false;
        }
    }
};
</script>

<style scoped>
.map-wrapper {
    display: flex;
    height: 500px;
    gap: 10px;
}

.map-container {
    flex: 1;
    position: relative;
}

.map {
    width: 100%;
    height: 100%;
}

.search-box {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 100;
    width: 300px;
}

.search-results {
    max-height: 300px;
    overflow-y: auto;
    background: #fff;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    margin-top: 5px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-item {
    padding: 10px 15px;
    cursor: pointer;
    border-bottom: 1px solid #eee;
}

.search-item:hover {
    background: #f5f7fa;
}

.search-item:last-child {
    border-bottom: none;
}

.search-item .name {
    font-weight: bold;
    color: #333;
    margin-bottom: 4px;
}

.search-item .address {
    font-size: 12px;
    color: #999;
}

.current-address {
    position: absolute;
    bottom: 10px;
    left: 10px;
    z-index: 100;
    background: #fff;
    padding: 8px 15px;
    border-radius: 4px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    max-width: 400px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.poi-panel {
    width: 320px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    background: #fff;
}

.poi-header {
    padding: 12px 15px;
    border-bottom: 1px solid #dcdfe6;
    background: #f5f7fa;
}

.poi-title {
    font-weight: bold;
    color: #333;
}

.poi-tip {
    font-size: 12px;
    color: #999;
    margin-left: 8px;
}

.poi-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
}

.no-poi {
    text-align: center;
    color: #999;
    padding: 40px 20px;
    font-size: 14px;
}

.poi-item {
    display: flex;
    padding: 10px;
    cursor: pointer;
    border-radius: 4px;
    margin-bottom: 8px;
    transition: all 0.2s;
    border: 1px solid transparent;
}

.poi-item:hover {
    background: #f5f7fa;
    border-color: #dcdfe6;
}

.poi-item.active {
    background: #ecf5ff;
    border-color: #409eff;
}

.poi-index {
    width: 24px;
    height: 24px;
    background: #409eff;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: bold;
    flex-shrink: 0;
    margin-right: 10px;
}

.poi-item.active .poi-index {
    background: #67c23a;
}

.poi-content {
    flex: 1;
    overflow: hidden;
}

.poi-name {
    font-weight: bold;
    color: #333;
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.poi-address {
    font-size: 12px;
    color: #666;
    margin-bottom: 2px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.poi-distance {
    font-size: 12px;
    color: #999;
}
</style>
