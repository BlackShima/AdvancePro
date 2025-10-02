package se233.chapter3.controller;

import java.io.File;

// 1. สร้างคลาส Exception ของเราเองโดยสืบทอดจาก Exception
public class ConversionException extends Exception {

    // 2. สร้าง Constructor เพื่อรับข้อความ error
    public ConversionException(String message) {
        super(message);
    }

    // 3. (แนะนำ) สร้าง Constructor ที่สามารถรับ Exception เดิมมาเป็น "สาเหตุ" (cause) ได้
    //    เพื่อที่เราจะไม่เสียข้อมูล error ดั้งเดิมไป
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}