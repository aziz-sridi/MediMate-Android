package com.example.medimate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Users Table
    private static final String TABLE_USERS = "allusers";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_USER_NAME = "userName";
    private static final String COLUMN_PASSWORD = "password";

    // Medications Table
    private static final String TABLE_MEDICATIONS = "medications";
    private static final String COLUMN_MED_ID = "med_id";
    private static final String COLUMN_MED_NAME = "med_name";
    private static final String COLUMN_IS_CHRONIC = "is_chronic";
    private static final String COLUMN_DOSES_PER_DAY = "doses_per_day";
    private static final String COLUMN_SET_TIMES = "set_times";

    // Appointments Table
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COLUMN_APPOINTMENT_ID = "id";
    private static final String COLUMN_APPOINTMENT_TITLE = "title";
    private static final String COLUMN_APPOINTMENT_DATE = "date";
    private static final String COLUMN_APPOINTMENT_TIME = "time";
    private static final String COLUMN_APPOINTMENT_LOCATION = "location";

    public DataBaseHelper(Context context) {
        super(context, "medimate.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS +
                "(" + COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT," +
                "is_logged_in INTEGER DEFAULT 0)";
        db.execSQL(createUsersTable);

        // Create Medications Table
        String createMedicationsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MEDICATIONS +
                "(" + COLUMN_MED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MED_NAME + " TEXT, " +
                COLUMN_IS_CHRONIC + " INTEGER, " +
                COLUMN_DOSES_PER_DAY + " INTEGER, " +
                COLUMN_SET_TIMES + " TEXT)";
        db.execSQL(createMedicationsTable);

        // Create Appointments Table
        String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_APPOINTMENTS +
                "(" + COLUMN_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_APPOINTMENT_TITLE + " TEXT, " +
                COLUMN_APPOINTMENT_DATE + " TEXT, " +
                COLUMN_APPOINTMENT_TIME + " TEXT, " +
                COLUMN_APPOINTMENT_LOCATION + " TEXT)";
        db.execSQL(createAppointmentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    public boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();

        return result != -1;
    }

    public boolean checkmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});

        boolean result = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return result;
    }

    public boolean checkemailpassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, hashPassword(password)});

        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return result;
    }

    private String hashPassword(String password) {
        // Implement a secure hashing algorithm (e.g., bcrypt) here
        // For example, you can use: String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return password; // For demonstration purposes, returning the password as-is
    }

    public void setUserLoggedOut(String userEmail) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("is_logged_in", 0);
            db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{userEmail});
            db.close();
        } catch (SQLiteException e) {
            // Handle the exception (e.g., log or display an error message)
            e.printStackTrace();
        }
    }

    public boolean saveMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MED_NAME, medication.getName());
        contentValues.put(COLUMN_IS_CHRONIC, medication.isChronic() ? 1 : 0);
        contentValues.put(COLUMN_DOSES_PER_DAY, medication.getDosesPerDay());
        String setTimesString = String.join(",", medication.getSetTimes());
        contentValues.put(COLUMN_SET_TIMES, setTimesString);
        long id = db.insert(TABLE_MEDICATIONS, null, contentValues);
        db.close();
        return id != -1;
    }

    public List<Medication> getAllMedicationsWithTimeRemaining() {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICATIONS, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_MED_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_MED_NAME);
                int isChronicIndex = cursor.getColumnIndex(COLUMN_IS_CHRONIC);
                int dosesPerDayIndex = cursor.getColumnIndex(COLUMN_DOSES_PER_DAY);
                int setTimesIndex = cursor.getColumnIndex(COLUMN_SET_TIMES);

                if (idIndex >= 0 && nameIndex >= 0 && isChronicIndex >= 0 && dosesPerDayIndex >= 0 && setTimesIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    boolean isChronic = cursor.getInt(isChronicIndex) == 1;
                    int dosesPerDay = cursor.getInt(dosesPerDayIndex);
                    String setTimesString = cursor.getString(setTimesIndex);
                    List<String> setTimes = Arrays.asList(setTimesString.split(","));

                    // Calculate time remaining for the next pill

                    Medication medication = new Medication(name, isChronic, dosesPerDay, setTimes);
                    medication.setId(id);
                    medication.setTimeRemaining();

                    medications.add(medication);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return medications;
    }

    public boolean deleteMedication(int medId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_MED_ID + " = ?";
        String[] selectionArgs = {String.valueOf(medId)};

        try {
            int deletedRows = db.delete(TABLE_MEDICATIONS, selection, selectionArgs);
            db.close();
            return deletedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DOSES_PER_DAY, medication.getDosesPerDay());
        values.put(COLUMN_SET_TIMES, convertListToString(medication.getSetTimes()));

        String selection = COLUMN_MED_ID + " = ?";
        String[] selectionArgs = {String.valueOf(medication.getId())};

        try {
            int rowsAffected = db.update(TABLE_MEDICATIONS, values, selection, selectionArgs);
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String convertListToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        for (String item : list) {
            result.append(item).append(",");
        }
        // Remove the trailing comma, if any
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    public boolean insertAppointment(String title, String date, String time, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_APPOINTMENT_TITLE, title);
        contentValues.put(COLUMN_APPOINTMENT_DATE, date);
        contentValues.put(COLUMN_APPOINTMENT_TIME, time);
        contentValues.put(COLUMN_APPOINTMENT_LOCATION, location);

        long id = db.insert(TABLE_APPOINTMENTS, null, contentValues);
        db.close();

        return id != -1;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APPOINTMENTS, null);

        if (cursor.moveToFirst()) {
            do {
                int titleIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_TITLE);
                int dateIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_DATE);
                int timeIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_TIME);
                int locationIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_LOCATION);
                int appointmentIdIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_ID); // Add this line

                if (titleIndex >= 0 && dateIndex >= 0 && timeIndex >= 0 && locationIndex >= 0) {
                    String title = cursor.getString(titleIndex);
                    String date = cursor.getString(dateIndex);
                    String time = cursor.getString(timeIndex);
                    String location = cursor.getString(locationIndex);

                    Appointment appointment = new Appointment(title, date, time, location);

                    // Set the appointment ID
                    if (appointmentIdIndex >= 0) {
                        int appointmentId = cursor.getInt(appointmentIdIndex);
                        appointment.setId(appointmentId);
                    }

                    appointments.add(appointment);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return appointments;
    }

    public boolean deleteAppointment(int appointmentId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_APPOINTMENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(appointmentId)};

        try {
            int deletedRows = db.delete(TABLE_APPOINTMENTS, selection, selectionArgs);
            db.close();
            return deletedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Appointment getAppointmentById(int appointmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APPOINTMENT_ID + "=?", new String[]{String.valueOf(appointmentId)});

        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_TITLE);
            int dateIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_DATE);
            int timeIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_TIME);
            int locationIndex = cursor.getColumnIndex(COLUMN_APPOINTMENT_LOCATION);

            if (titleIndex >= 0 && dateIndex >= 0 && timeIndex >= 0 && locationIndex >= 0) {
                String title = cursor.getString(titleIndex);
                String date = cursor.getString(dateIndex);
                String time = cursor.getString(timeIndex);
                String location = cursor.getString(locationIndex);

                cursor.close();
                db.close();

                return new Appointment(title, date, time, location);
            }
        }

        cursor.close();
        db.close();

        return null;
    }

    public boolean updateAppointment(int appointmentId, String newLocation, String newDate, String newTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_APPOINTMENT_DATE, newDate);
        contentValues.put(COLUMN_APPOINTMENT_TIME, newTime);
        contentValues.put(COLUMN_APPOINTMENT_LOCATION, newLocation);

        String selection = COLUMN_APPOINTMENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(appointmentId)};

        try {
            int updatedRows = db.update(TABLE_APPOINTMENTS, contentValues, selection, selectionArgs);
            db.close();

            if (updatedRows > 0) {
                return true;
            } else {
                // Log an error message to help identify the issue
                Log.e("UpdateAppointment", "No rows updated. Appointment ID: " + appointmentId);
                return false;
            }
        } catch (Exception e) {
            // Log any exception that might occur during the update
            e.printStackTrace();
            Log.e("UpdateAppointment", "Exception during update. Appointment ID: " + appointmentId);
            return false;
        }
    }
}
