package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业统计报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        // 通过循环，获取从begin到end范围内的所有日期，存入集合
        while (!begin.equals(end)) {
            // 日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 根据日期集合，查询每天对应的营业额，存入对应的集合
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询date对应的营业额（状态为已完成的订单）
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 调用Mapper方法查询指定日期的营业额
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", 5);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        // 将日期集合和营业额集合转换为TurnoverReportVO对象并返回
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        // 通过循环，获取从begin到end范围内的所有日期，存入集合
        while (!begin.equals(end)) {
            // 日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 存放每天新增用户数量的集合
        List<Integer> newUserList = new ArrayList<>();
        // 存放每天累计用户数量的集合
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map totalmap = new HashMap();
            totalmap.put("endTime", endTime);

            // 查询总用户数量
            Integer totalUsercount = userMapper.countByMap(totalmap);

            Map newmap = new HashMap();
            newmap.put("beginTime", beginTime);
            newmap.put("endTime", endTime);

            // 查询新增用户数量
            Integer newUserCount = userMapper.countByMap(newmap);

            totalUserList.add(totalUsercount);
            newUserList.add(newUserCount);
        }

        // 封装并返回结果
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();

    }

    /**
     * 订单统计报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        // 通过循环，获取从begin到end范围内的所有日期，存入集合
        while (!begin.equals(end)) {
            // 日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        // 遍历dataList集合，查询明天的有效订单数和订单总数
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询订单总数
            Integer orderCount = geyOrderCount(beginTime, endTime, null);
            orderCountList.add(orderCount);

            // 查询有效订单数
            Integer validOrderCount = geyOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(validOrderCount);
        }

        // 计算时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        // 计算时间区间内的有效订单总数
        Integer totalValidOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // 计算有效订单占比
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (totalValidOrderCount.doubleValue() / totalOrderCount.doubleValue());
        }

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

        return orderReportVO;
    }

    //
    private Integer geyOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("beginTime", begin);
        map.put("endTime", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    /**
     * 销售统计报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return
     */
    public SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> nameList = salesTop10.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList());
        String salesTop10Name = StringUtils.join(nameList, ",");

        List<Integer> numberList = salesTop10.stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList());
        String salesTop10Number = StringUtils.join(numberList, ",");

        return SalesTop10ReportVO.builder()
                .nameList(salesTop10Name)
                .numberList(salesTop10Number)
                .build();
    }

    /**
     * 导出营业数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        // 查询数据库，获取报表数据
        LocalDate beginDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);

        LocalDateTime beginDateTime = LocalDateTime.of(beginDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        BusinessDataVO summaryData = workSpaceService.getBusinessData(beginDateTime, endDateTime);

        // 通过POI将数据写入Excel文件
        try (InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx")) {
            if (in == null) {
                throw new IllegalStateException("报表模板文件不存在：template/运营数据报表模板.xlsx");
            }

            // 基于模板创建新的Excel文件
            XSSFWorkbook workbook = new XSSFWorkbook(in);

            // 获取表格文件的Sheet页
            XSSFSheet sheet = workbook.getSheet("Sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间: " + beginDate + " 至 " + endDate);

            // 填充数据--营业额
            sheet.getRow(3).getCell(2).setCellValue(summaryData.getTurnover());

            // 填充数据--订单完成率
            sheet.getRow(3).getCell(4).setCellValue(summaryData.getOrderCompletionRate());

            // 填充数据--新增用户数
            sheet.getRow(3).getCell(6).setCellValue(summaryData.getNewUsers());

            // 填充数据--有效订单数
            sheet.getRow(4).getCell(2).setCellValue(summaryData.getValidOrderCount());

            // 填充数据--平均客单价
            sheet.getRow(4).getCell(4).setCellValue(summaryData.getUnitPrice());

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = beginDate.plusDays(i);

                // 查询某一天的营业数据
                BusinessDataVO dailyData = workSpaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));

                // 获得某一行
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(dailyData.getTurnover());
                row.getCell(3).setCellValue(dailyData.getValidOrderCount());
                row.getCell(4).setCellValue(dailyData.getOrderCompletionRate());
                row.getCell(5).setCellValue(dailyData.getUnitPrice());
                row.getCell(6).setCellValue(dailyData.getNewUsers());
            }

            // 通过数据流将Excel文件下载到客户端
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);

            // 关闭资源
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
