package com.example.gas.api.service.iml;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.example.gas.api.db.dao.GasWorkerInspectionReportDao;
import com.example.gas.api.db.dao.GasWorkerPlanDao;
import com.example.gas.api.service.GasWorkerInspectionReportService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class GasWorkerInspectionReportServiceImpl implements GasWorkerInspectionReportService {

    @Resource
    private GasWorkerInspectionReportDao gasWorkerInspectionReportDao;

    @Resource
    private GasWorkerPlanDao gasWorkerPlanDao;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public byte[] generateInspectionReport(Map param) throws Exception {
        List<HashMap> reportList = gasWorkerInspectionReportDao.searchInspectionReportList(param);
        
        if (reportList == null || reportList.isEmpty()) {
            throw new RuntimeException("没有找到符合条件的巡检记录");
        }

        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1200));
            pageMar.setRight(BigInteger.valueOf(1200));
            pageMar.setTop(BigInteger.valueOf(1000));
            pageMar.setBottom(BigInteger.valueOf(1000));

            XWPFFooter footer = doc.createFooter(HeaderFooterType.DEFAULT);
            XWPFParagraph footerParagraph = footer.createParagraph();
            footerParagraph.setAlignment(ParagraphAlignment.CENTER);
            footerParagraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");

            for (int i = 0; i < reportList.size(); i++) {
                HashMap report = reportList.get(i);
                HashMap reportParam = buildReportParam(report);
                
                createCover(doc, reportParam);
                createWelcome(doc, reportParam);
                createCustomerInfo(doc, reportParam);
                createInspectionContent(doc, report);
                
                if (i < reportList.size() - 1) {
                    addPageBreak(doc);
                }
            }
            
            doc.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generateInspectionReportByPlanId(Integer planId) throws Exception {
        HashMap report = gasWorkerInspectionReportDao.searchInspectionReportDetail(planId);
        
        if (report == null || report.isEmpty()) {
            throw new RuntimeException("没有找到该巡检记录");
        }

        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1200));
            pageMar.setRight(BigInteger.valueOf(1200));
            pageMar.setTop(BigInteger.valueOf(1000));
            pageMar.setBottom(BigInteger.valueOf(1000));

            XWPFFooter footer = doc.createFooter(HeaderFooterType.DEFAULT);
            XWPFParagraph footerParagraph = footer.createParagraph();
            footerParagraph.setAlignment(ParagraphAlignment.CENTER);
            footerParagraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");

            HashMap reportParam = buildReportParam(report);
            
            createCover(doc, reportParam);
            createWelcome(doc, reportParam);
            createCustomerInfo(doc, reportParam);
            createInspectionContent(doc, report);
            
            doc.write(out);
            return out.toByteArray();
        }
    }

    private HashMap buildReportParam(HashMap report) {
        HashMap param = new HashMap();
        
        String userName = MapUtil.getStr(report, "userName");
        String userPhone = MapUtil.getStr(report, "userPhone");
        String userAddress = MapUtil.getStr(report, "userAddress");
        String buildingInfo = MapUtil.getStr(report, "buildingInfo");
        String workerName = MapUtil.getStr(report, "workerName");
        String deptName = MapUtil.getStr(report, "deptName");
        String deptSubName = MapUtil.getStr(report, "deptSubName");
        String planDate = MapUtil.getStr(report, "planDate");
        String inspectDate = MapUtil.getStr(report, "inspectDate");
        String inspectDesc = MapUtil.getStr(report, "inspectDesc");
        String inspectResult = MapUtil.getStr(report, "inspectResult");
        Integer hasDanger = MapUtil.getInt(report, "hasDanger");
        
        List<HashMap> item = new ArrayList<>();
        item.add(new HashMap() {{ put("label", "姓    名："); put("value", userName != null ? userName : "-"); }});
        item.add(new HashMap() {{ put("label", "巡检员："); put("value", workerName != null ? workerName : "-"); }});
        item.add(new HashMap() {{ put("label", "单    位："); put("value", deptName != null ? deptName : "-"); }});
        item.add(new HashMap() {{ put("label", "日    期："); put("value", planDate != null ? planDate.replace("-", "/") : "-"); }});
        
        param.put("item", item);
        param.put("name", userName != null ? userName : "用户");
        param.put("workerName", workerName != null ? workerName : "-");
        param.put("deptName", deptName != null ? deptName : "-");
        param.put("deptSubName", deptSubName != null ? deptSubName : "-");
        param.put("tel", userPhone != null ? userPhone : "-");
        param.put("date", planDate != null ? planDate.replace("-", "/") : "-");
        param.put("address", formatAddress(userAddress, buildingInfo));
        param.put("inspectDate", inspectDate != null ? inspectDate.replace("T", " ") : "-");
        param.put("inspectDesc", inspectDesc != null ? inspectDesc : "-");
        param.put("inspectResult", inspectResult != null ? inspectResult : "未巡检");
        param.put("hasDanger", hasDanger);
        
        return param;
    }

    private String formatAddress(String address, String buildingInfo) {
        if (StrUtil.isNotBlank(address) && StrUtil.isNotBlank(buildingInfo)) {
            return address + " " + buildingInfo;
        }
        return StrUtil.isNotBlank(address) ? address : (StrUtil.isNotBlank(buildingInfo) ? buildingInfo : "-");
    }

    private void createCover(XWPFDocument doc, HashMap param) throws Exception {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(20);
        run.setFontFamily("Microsoft YaHei");
        run.setUnderline(UnderlinePatterns.THICK);
        run.setText("重庆市梁平燃气中心");
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(480));

        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(10);
        run.setFontFamily("Microsoft YaHei");
        run.setText("Chongqing Liangping Gas Center");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(280));

        paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(26);
        run.setFontFamily("Microsoft YaHei");
        run.setText("巡检报告书");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(800));
        ctSpacing.setAfterLines(BigInteger.valueOf(800));

        XWPFTable table = doc.createTable(4, 2);
        table.setTableAlignment(TableRowAlign.CENTER);
        table.getCTTbl().getTblPr().unsetTblBorders();

        List<HashMap> list = (List<HashMap>) param.get("item");
        for (int i = 0; i < list.size(); i++) {
            HashMap map = list.get(i);
            String label = MapUtil.getStr(map, "label");
            String value = MapUtil.getStr(map, "value");
            XWPFTableRow row = table.getRow(i);
            row.setHeight(600);
            List<XWPFTableCell> tableCells = row.getTableCells();
            
            CTTcPr ctTcPr = tableCells.get(1).getCTTc().addNewTcPr();
            CTTblWidth ctTblWidth = ctTcPr.addNewTcW();
            ctTblWidth.setW(BigInteger.valueOf(2800));

            paragraph = tableCells.get(0).getParagraphArray(0);
            run = paragraph.createRun();
            run.setFontFamily("Microsoft YaHei");
            run.setText(label + "    ");

            paragraph = tableCells.get(1).getParagraphArray(0);
            paragraph.setBorderBottom(Borders.BABY_RATTLE);
            run = paragraph.createRun();
            run.setFontFamily("Microsoft YaHei");
            run.setText("    " + value);
        }
    }

    private void createWelcome(XWPFDocument doc, HashMap param) {
        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontSize(18);
        run.setFontFamily("Microsoft YaHei");
        run.setText("燃气巡检报告");

        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(420));
        ctSpacing.setBeforeLines(BigInteger.valueOf(2000));
        ctSpacing.setAfterLines(BigInteger.valueOf(200));

        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(10);
        run.setFontFamily("Microsoft YaHei");
        String name = MapUtil.getStr(param, "name");
        run.setText("尊敬的" + name + "，您好！");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(380));
        ctSpacing.setAfterLines(BigInteger.valueOf(50));

        paragraph = doc.createParagraph();
        paragraph.setIndentationFirstLine(600);
        run = paragraph.createRun();
        run.setFontSize(10);
        run.setFontFamily("Microsoft YaHei");
        run.setText("感谢您对梁平燃气的支持。现将您的燃气巡检结果汇总如下，请您认真阅读巡检结果和建议。如有疑问，请您来中心或者致电本中心服务电话，我们将安排专业人员为您答疑解惑。欢迎对我们的工作提出批评和建议。祝您生活愉快！");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(380));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));
    }

    private void createCustomerInfo(XWPFDocument doc, HashMap param) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setFontSize(14);
        run.setFontFamily("Microsoft YaHei");
        run.setText("燃气用户信息");
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(200));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));

        XWPFTable table = doc.createTable(4, 6);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(9850));

        String name = MapUtil.getStr(param, "name");
        String tel = MapUtil.getStr(param, "tel");
        String date = MapUtil.getStr(param, "date");
        String address = MapUtil.getStr(param, "address");
        String workerName = MapUtil.getStr(param, "workerName");
        String deptName = MapUtil.getStr(param, "deptName");

        String[][] data = {
            {"姓名", name, "巡检员", workerName, "单位", deptName},
            {"电话", tel, "巡检日期", date, "", ""},
            {"地址", address, "", "", "", ""},
            {"备注", "该次巡检主要是检查：1、用气安全概况；2、燃气设备运行情况；", "", "", "", ""}
        };

        for (int i = 0; i < 4; i++) {
            XWPFTableRow row = table.getRow(i);
            row.setHeight(550);
            List<XWPFTableCell> tableCells = row.getTableCells();
            
            for (int j = 0; j < 6; j++) {
                XWPFTableCell cell = tableCells.get(j);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                
                if (j % 2 == 0) {
                    cell.setColor("f0f0f0");
                }
                
                paragraph = cell.getParagraphArray(0);
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                run = paragraph.createRun();
                run.setFontSize(9);
                run.setFontFamily("Microsoft YaHei");
                run.setText(data[i][j]);
            }
        }

        mergeCellsHorizontally(table, 2, 1, 5);
        mergeCellsHorizontally(table, 3, 1, 5);
    }

    private void createInspectionContent(XWPFDocument doc, HashMap report) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setFontSize(14);
        run.setFontFamily("Microsoft YaHei");
        run.setText("巡检结果");
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(200));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));

        Integer hasDanger = MapUtil.getInt(report, "hasDanger");
        String inspectResult = MapUtil.getStr(report, "inspectResult");
        String inspectDesc = MapUtil.getStr(report, "inspectDesc");
        String inspectDate = MapUtil.getStr(report, "inspectDate");
        String inspectPhoto = MapUtil.getStr(report, "inspectPhoto");
        
        Map<String, String> itemDetails = parseInspectDesc(inspectDesc);
        int photoCount = countPhotos(inspectPhoto);

        XWPFTable table = doc.createTable(5, 4);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(9850));

        String[][] data = {
            {"巡检状态", inspectResult != null ? inspectResult : "未巡检", "隐患情况", hasDanger != null ? (hasDanger == 1 ? "无隐患" : "存在隐患") : "-"},
            {"巡检时间", inspectDate != null ? inspectDate.replace("T", " ") : "-", "", ""},
            {"隐患描述", inspectDesc != null ? inspectDesc : "-", "", ""},
            {"整改建议", hasDanger != null && hasDanger == 0 ? "建议及时整改，消除安全隐患" : "-", "", ""},
            {"巡检员签字", "", "审核签字", ""}
        };

        for (int i = 0; i < 5; i++) {
            XWPFTableRow row = table.getRow(i);
            row.setHeight(550);
            List<XWPFTableCell> tableCells = row.getTableCells();
            
            for (int j = 0; j < 4; j++) {
                XWPFTableCell cell = tableCells.get(j);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                
                if (j % 2 == 0) {
                    cell.setColor("f0f0f0");
                }
                
                paragraph = cell.getParagraphArray(0);
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                run = paragraph.createRun();
                run.setFontSize(9);
                run.setFontFamily("Microsoft YaHei");
                run.setText(data[i][j]);
            }
        }

        mergeCellsHorizontally(table, 1, 1, 3);
        mergeCellsHorizontally(table, 2, 1, 3);
        mergeCellsHorizontally(table, 3, 1, 3);

        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(12);
        run.setFontFamily("Microsoft YaHei");
        run.setText("巡检环节明细");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(200));
        ctSpacing.setAfterLines(BigInteger.valueOf(50));

        String[] itemNames = {"燃气管道", "燃气表", "燃气具", "连接配件", "使用环境"};
        XWPFTable detailTable = doc.createTable(6, 3);
        CTTblPr detailTblPr = detailTable.getCTTbl().getTblPr();
        detailTblPr.getTblW().setType(STTblWidth.DXA);
        detailTblPr.getTblW().setW(BigInteger.valueOf(9850));

        XWPFTableRow headerRow = detailTable.getRow(0);
        headerRow.setHeight(450);
        String[] headers = {"巡检环节", "隐患情况", "整改措施"};
        for (int i = 0; i < 3; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.setColor("4472C4");
            paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            run.setBold(true);
            run.setColor("FFFFFF");
            run.setText(headers[i]);
        }

        for (int i = 0; i < 5; i++) {
            XWPFTableRow row = detailTable.getRow(i + 1);
            row.setHeight(450);
            String itemName = itemNames[i];
            String itemDesc = itemDetails.get(itemName);
            boolean hasItemDanger = itemDesc != null && !itemDesc.isEmpty();
            
            String[] rowData = {
                itemName,
                hasItemDanger ? "有隐患" : "无隐患",
                hasItemDanger ? itemDesc : "-"
            };
            
            for (int j = 0; j < 3; j++) {
                XWPFTableCell cell = row.getCell(j);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                if (j == 0) {
                    cell.setColor("f0f0f0");
                }
                paragraph = cell.getParagraphArray(0);
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                run = paragraph.createRun();
                run.setFontSize(9);
                run.setFontFamily("Microsoft YaHei");
                run.setText(rowData[j]);
            }
        }

        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("巡检照片：" + (photoCount > 0 ? "已拍摄 " + photoCount + " 张照片" : "无"));
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(100));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));
    }

    private Map<String, String> parseInspectDesc(String inspectDesc) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] itemNames = {"燃气管道", "燃气表", "燃气具", "连接配件", "使用环境"};
        for (String name : itemNames) {
            result.put(name, null);
        }
        
        if (StrUtil.isBlank(inspectDesc)) {
            return result;
        }
        
        String[] lines = inspectDesc.split("\n");
        for (String line : lines) {
            if (StrUtil.isBlank(line)) continue;
            int start = line.indexOf("【");
            int end = line.indexOf("】");
            if (start >= 0 && end > start) {
                String itemName = line.substring(start + 1, end);
                String desc = line.substring(end + 1).trim();
                if (result.containsKey(itemName)) {
                    result.put(itemName, desc);
                }
            }
        }
        return result;
    }

    private int countPhotos(String inspectPhoto) {
        if (StrUtil.isBlank(inspectPhoto)) {
            return 0;
        }
        String[] photos = inspectPhoto.split(",");
        int count = 0;
        for (String photo : photos) {
            if (StrUtil.isNotBlank(photo.trim())) {
                count++;
            }
        }
        return count;
    }

    private void mergeCellsHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        XWPFTableCell cell = table.getRow(row).getCell(fromCol);
        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        tcPr.addNewHMerge().setVal(STMerge.RESTART);
        
        for (int i = fromCol + 1; i <= toCol; i++) {
            XWPFTableCell mergeCell = table.getRow(row).getCell(i);
            CTTcPr mergeTcPr = mergeCell.getCTTc().addNewTcPr();
            mergeTcPr.addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    private void addPageBreak(XWPFDocument doc) {
        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setPageBreak(true);
    }

    @Override
    @Transactional
    public String generateAndUploadReport(Integer planId) throws Exception {
        byte[] docxContent = generateInspectionReportByPlanId(planId);
        
        String objectName = "/report/inspection/" + planId + ".docx";
        
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(docxContent)) {
                client.putObject(
                        PutObjectArgs.builder()
                                .bucket("report")
                                .object(objectName)
                                .stream(inputStream, docxContent.length, -1)
                                .contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                .build()
                );
            }

            Map<String, Object> param = new HashMap<>();
            param.put("id", planId);
            param.put("filePath", objectName);
            gasWorkerPlanDao.updateFilePath(param);

            log.info("巡检报告上传成功，planId：{}，文件路径：{}", planId, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("上传巡检报告到Minio失败", e);
            throw new RuntimeException("上传巡检报告失败: " + e.getMessage());
        }
    }
}
