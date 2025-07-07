package ClientServer;

public enum InquiryManagerActions {
    ALL_INQUIRY,                // הצגת כל הפניות
    ADD_INQUIRY,                // הוספת פנייה
    GET_INQUIRY_STATUS,         // קבלת סטטוס של פנייה לפי קוד
    CANCEL_INQUIRY,            // ביטול פנייה
    ADD_REPRESENTATIVE,        // כניסת נציג
    DELETE_REPRESENTATIVE,     // יציאת נציג
    IS_REPRESENTATIVE_ACTIVE,  // בדיקת האם נציג פעיל
    GET_REPRESENTATIVE_INQUIRIES, // קבלת כל הפניות של נציג
    GET_REPRESENTATIVE_NAME_BY_INQUIRY_CODE // קבלת שם נציג לפי מספר פנייה
}

