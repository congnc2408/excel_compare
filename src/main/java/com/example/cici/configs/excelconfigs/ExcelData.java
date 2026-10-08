package com.example.cici.configs.excelconfigs;

import java.util.List;
import java.util.Map;

public record ExcelData(List<String> headers, List<Map<String, Object>> body) {
}
