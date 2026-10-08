package com.example.cici.configs.excelconfigs;

public class ExcelComparator {
    public static boolean isDifferent(Object val1, Object val2) {
        String s1 = (val1 == null) ? "" : val1.toString().trim();
        String s2 = (val2 == null) ? "" : val2.toString().trim();
        if (s1.isEmpty() && s2.isEmpty()) {
            return false;
        }
        return !s1.equals(s2);
    }

    public static boolean hasDifferent(ExcelData file1Data, ExcelData file2Data, int rowIndex, String headerKey) {
        if (file1Data == null || file2Data == null) {
            return false;
        }
        int minRow = Math.min(file1Data.body().size(), file2Data.body().size());

        if (rowIndex >= minRow) {
            return false;
        }

        Object val1 = file1Data.body().get(rowIndex).get(headerKey);
        Object val2 = file2Data.body().get(rowIndex).get(headerKey);

        return isDifferent(val1, val2);
    }
}