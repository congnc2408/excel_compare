package com.example.cici.configs.excelconfigs;

public class ExcelComparator {
    private static final int MAX_LOOK_AHEAD = 100;
//    public static boolean isDifferent(Object val1, Object val2) {
//        String s1 = (val1 == null) ? "" : val1.toString().trim();
//        String s2 = (val2 == null) ? "" : val2.toString().trim();
//        if (s1.isEmpty() && s2.isEmpty()) {
//            return false;
//        }
//        return !s1.equals(s2);
//    }
//
//    public static boolean hasDifferent(ExcelData file1Data, ExcelData file2Data, int rowIndex, String headerKey) {
//        if (file1Data == null || file2Data == null) {
//            return false;
//        }
//        int minRow = Math.min(file1Data.body().size(), file2Data.body().size());
//
//        if (rowIndex >= minRow) {
//            return false;
//        }
//
//        Object val1 = file1Data.body().get(rowIndex).get(headerKey);
//        Object val2 = file2Data.body().get(rowIndex).get(headerKey);
//
//        return isDifferent(val1, val2);
//    }

        public static boolean isDifferent(Object val1, Object val2) {
        String s1 = (val1 == null) ? "" : val1.toString().trim();
        String s2 = (val2 == null) ? "" : val2.toString().trim();
        if (s1.isEmpty() && s2.isEmpty()) {
            return false;
        }
        return !s1.equals(s2);
    }

    public static boolean hasDifferent(ExcelData file1Data, ExcelData file2Data, int rowIndex, String headerKey) {
        if (file1Data == null || file2Data == null || rowIndex >= file1Data.body().size()) {
            return false;
        }
        Object val1 = file1Data.body().get(rowIndex).get(headerKey);
        if (rowIndex >= file2Data.body().size()) return isDifferent(val1,null);
        Object val2 = file2Data.body().get(rowIndex).get(headerKey);

        if (!isDifferent(val1, val2)) return false;

        String s1 = (val1 == null) ? "" :val1.toString().trim();
        if (s1.isEmpty()) return true;

        int minRow = Math.min(file1Data.body().size(), file2Data.body().size());

//        if (rowIndex >= minRow) {
//            return false;
//        }
        for (int i = rowIndex+1; i < minRow; i++) {
            Object nextVal2 = file2Data.body().get(i).get(headerKey);

            if (!isDifferent(val1, nextVal2)) {
                for (int j = 0; j < minRow; j++) {
                    Object nextVal1 = file1Data.body().get(j).get(headerKey);
                    if (!isDifferent(nextVal1, nextVal2)) return false;
                }
            }
        }

//        while (isDifferent(val1, val2)){
//            Object nextVal1 = file1Data.body().get(file1Data.body().size()).get(headerKey);
//            Object nextVal2 = file2Data.body().get(file2Data.body().size()).get(headerKey);
//            if (!isDifferent(nextVal1, nextVal2)) {
//                return false;
//            }
//        }
        return true;
    }
}