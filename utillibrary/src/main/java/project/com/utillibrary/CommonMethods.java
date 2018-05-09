package project.com.utillibrary;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CommonMethods {

    final String TAG = "CommonMethods";

    Context context;
    Pattern pattern;
    Matcher matcher;
    final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[A-Z]).{8,20})"; //atleast 1 digit, atleast one capital letter, min 8 characters, max 20 characters

    public CommonMethods(Context context) {
        this.context = context;
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public Object[] getCursorValues(Cursor cursor, int columnCount) {
        Object[] objArr = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            objArr[i] = cursor.getString(i);
        }
        return objArr;
    }

    public int convertDpToPx(int padding_in_dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        return padding_in_px;
    }




    public String encodeTobase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            return imageEncoded;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Bitmap decodeBase64(String input) {
        try {
            byte[] decodedByte = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String convertCurrentCalendarToString(Calendar calendar, String outputFormat) {
        String output = "";
        try {
            SimpleDateFormat timeServerFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
            timeServerFormat.setTimeZone(TimeZone.getDefault());
            output = timeServerFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public Bitmap getCircleBitmap(Bitmap srcBitmap, int colorCode, int padding) {
        final Bitmap output = Bitmap.createBitmap(srcBitmap.getWidth() + padding, srcBitmap.getHeight() + padding, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(colorCode);
        canvas.drawOval(rectF, paint);
        Matrix matrix = new Matrix();
        // Draw the bitmap at the center position of the canvas both vertically and horizontally
        matrix.postTranslate(
                canvas.getWidth() / 2 - srcBitmap.getWidth() / 2,
                canvas.getHeight() / 2 - srcBitmap.getHeight() / 2
        );
        canvas.drawBitmap(srcBitmap, matrix, null);

        srcBitmap.recycle();

        return output;
    }

    public Bitmap getCircleBitmap(String colorCode, int size) {
        final Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(colorCode));
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(output, rect, rect, paint);

//        bitmap.recycle();
        return output;
    }

    public boolean isStringEmpty(String s) {
        if (s == null)
            return true;
        else if (s.toString().trim().equals(""))
            return true;
        else if (s.toString().trim().equalsIgnoreCase("null"))
            return true;
        return false;
    }

    public String fromHtml(String html) {
        if (!isStringEmpty(html)) {
            Spanned result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
            if (result != null) {
                return result.toString();
            }
        }
        return "";
    }

    public boolean containsInvalidDecimalData(String s) {
        try {
            if (Double.parseDouble(s) <= 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public boolean isEditTextEmpty(EditText et) {
        if (et == null)
            return true;
        else if (et.getText() == null)
            return true;
        else if (et.getText().toString().trim().equals(""))
            return true;
        return false;
    }

    public boolean isTextViewEmpty(TextView et) {
        if (et == null)
            return true;
        else if (et.getText() == null)
            return true;
        else if (et.getText().toString().trim().equals(""))
            return true;
        return false;
    }

    public boolean containsSpecialCharacter(String s) {
        Pattern pattern = Pattern.compile("[a-zA-Z1-9 ]*");
        Matcher matcher = pattern.matcher(s);
        return !matcher.matches();
    }

    public boolean validPassword(String password) {
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public byte[] getDataFromFile(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void showSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validateEmail(final String hex) {
        matcher = pattern.matcher(hex);
        boolean temp = matcher.matches();
        return temp;
    }


    public Uri getOutputMediaFileUri(int type, String IMAGE_DIRECTORY_NAME, int MEDIA_TYPE_IMAGE) {
        return Uri.fromFile(getOutputMediaFile(type, IMAGE_DIRECTORY_NAME, MEDIA_TYPE_IMAGE));
    }

    public File getOutputMediaFile(int type, String IMAGE_DIRECTORY_NAME, int MEDIA_TYPE_IMAGE) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public boolean isDeviceSupportCamera() {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public String generateRandomFileName() {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image" + n + ".jpg";
        return fname;
    }

    public boolean isSameDate(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public String convertCurrentTimeZoneToUTC(Date date, SimpleDateFormat sourceDateFormat) {
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat dFormat = sourceDateFormat;
            dFormat.setTimeZone(tz);
            return dFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("country_code.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public File saveBitmapToFile(Bitmap b, String path) {
        File tempDir = new File(path);
        tempDir.mkdirs();
        File tempFile = new File(tempDir, generateRandomFileName());
        if (tempFile.exists()) tempFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile;
    }
    public File compressImage(File imageFile, int reqWidth, int reqHeight, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            // write the compressed bitmap at the destination specified by destinationPath.
            decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationPath);
    }
    Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        //check the rotation of the image and display it properly
        ExifInterface exif;
        exif = new ExifInterface(imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return scaledBitmap;
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static void showToast(String toastMesssage, Context context) {
        Toast toast = Toast.makeText(context, toastMesssage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 125);
        toast.getView().setPadding(40, 20, 40, 20);
        toast.getView().setBackgroundResource(R.drawable.rounded_background);
//        toast.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        toast.show();
    }





}
