package ottu.payment.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ottu.payment.R;
import ottu.payment.databinding.ItemPaymentMethodBinding;
import ottu.payment.model.fetchTxnDetail.PaymentMethod;
import ottu.payment.model.fetchTxnDetail.RespoFetchTxnDetail;
import ottu.payment.model.submitCHD.Card_SubmitCHD;
import ottu.payment.ui.PaymentActivity;

import static ottu.payment.util.Constant.CardListPosition;
import static ottu.payment.util.Constant.LocalLan;
import static ottu.payment.util.Constant.savedCardSelected;
import static ottu.payment.util.Constant.selectedCardPos;
import static ottu.payment.util.Util.listCardName;
import static ottu.payment.util.Util.listCardPatter;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

    ArrayList<PaymentMethod> listPaymentMethod;
    RespoFetchTxnDetail transactionDetail;
    private ItemPaymentMethodBinding binding;
    ItemPaymentMethodBinding itemBinding1;
    PaymentActivity context;


    int lastSeected = -1;
    SparseArray<Pattern> mCCPatterns = listCardPatter();
    final Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    String a;
    int keyDel;
    private boolean internalStopFormatFlag;


    public PaymentMethodAdapter(PaymentActivity paymentActivity, RespoFetchTxnDetail cards) {
        context = paymentActivity;
        listPaymentMethod = cards.payment_methods;

        transactionDetail = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPaymentMethodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.bindData(listPaymentMethod.get(position), position);

    }

    @Override
    public int getItemCount() {
        return listPaymentMethod.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPaymentMethodBinding itemBinding;
        public String SPACE_CHAR = " ";

        public ViewHolder(ItemPaymentMethodBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;

        }

        public void bindData(PaymentMethod paymentMethod, int position) {

            if (selectedCardPos == -1) {
                if (itemBinding1 != null) {
                    itemBinding1.layoutCardDetail.setVisibility(View.GONE);
                }
            }
            if (selectedCardPos == position) {
                itemBinding.layoutCardInfo.setBackground(context.getResources().getDrawable(R.drawable.item_bg_selected));
                setArrow(itemBinding.arrow,true,listPaymentMethod.get(position).code);
            } else {
                itemBinding.layoutCardInfo.setBackground(context.getResources().getDrawable(R.drawable.item_bg));
                setArrow(itemBinding.arrow,false,listPaymentMethod.get(position).code);
            }
            itemBinding.cardNumber.setText(listPaymentMethod.get(position).name);


            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream stream = new URL(listPaymentMethod.get(position).icon).openStream();

                        Bitmap image = BitmapFactory.decodeStream(stream);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                itemBinding.cardImage.setImageBitmap(image);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            itemBinding.nameTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 2) {
                        checkIfcardDetailfill(itemBinding1, true);
                    } else {
                        checkIfcardDetailfill(itemBinding1, false);
                    }
                }
            });

            itemBinding.cardNumberTextView.addTextChangedListener(new TextWatcher() {
                boolean considerChange = false;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (considerChange) {
                        int mDrawableResId = 0;
                        for (int n = 0; n < mCCPatterns.size(); n++) {
                            int key = mCCPatterns.keyAt(n);

                            // get the object by the key.
                            Pattern p = mCCPatterns.get(key);
                            Matcher m = p.matcher(charSequence);
                            if (m.find()) {
                                mDrawableResId = key;
                                CardListPosition = n;
                                break;
                            }
                        }
                        if (mDrawableResId > 0 && mDrawableResId != 0) {
//                            Drawable d = context.getResources().getDrawable(mDrawableResId);
//                            binding.cardIndicatorImage.setImageDrawable(d);
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mDrawableResId);
                            itemBinding.cardIndicatorImage.setImageBitmap(bitmap);

                        }
                    }
                    considerChange = !considerChange;

                    if (charSequence.length() > 13) {
                        checkIfcardDetailfill(itemBinding, true);
                    } else {
                        checkIfcardDetailfill(itemBinding, false);
                    }
                }


                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
                        String input = s.toString();
                        String numbersOnly = keepNumbersOnly(input);
                        String code = formatNumbersAsCode(numbersOnly);

                        itemBinding.cardNumberTextView.removeTextChangedListener(this);
                        itemBinding.cardNumberTextView.setText(code);
                        // You could also remember the previous position of the cursor
                        itemBinding.cardNumberTextView.setSelection(itemBinding.cardNumberTextView.getText().toString().length());
                        itemBinding.cardNumberTextView.addTextChangedListener(this);

                        int cursorPos = itemBinding.cardNumberTextView.getSelectionStart();
                        String ts = String.valueOf(code.charAt(cursorPos - 1));
                        boolean isspace = ts == SPACE_CHAR;
                        if (cursorPos > 0 && ts.equals(SPACE_CHAR) ) {
                            itemBinding.cardNumberTextView.setSelection(cursorPos - 1);
                        }

//                         String c = String.valueOf(code.charAt(code.length()));
                    }
                    if (s.length() > 14) {
                        itemBinding.cardNumberErrorTextView.setText("");
                    }

                }
            });

//            itemBinding.datetextView.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int start, int removed, int added) {
//
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    if (charSequence.length() > 4){
//                        checkIfcardDetailfill(itemBinding1, true);
//                    }else {
//                        checkIfcardDetailfill(itemBinding1,false);
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    if (internalStopFormatFlag) {
//                        return;
//                    }
//                    internalStopFormatFlag = true;
//                    formatExpiryDate(editable, 4);
//                    internalStopFormatFlag = false;
//                    if (editable.length() > 4){
//                        itemBinding1.expiredateErrorTextView.setText("");
//                    }
//
//                    if (editable.length() > 1){
//                    if (Integer.parseInt(String.valueOf(editable.charAt(0))) > 1 || Integer.parseInt(String.valueOf(editable.subSequence(0,2))) > 12){
//                        itemBinding1.expiredateErrorTextView.setText("Month is wrong");
//                    }else {
//                        itemBinding1.expiredateErrorTextView.setText("");
//                    }
//                    }
//
//
//
//                }
//            });
            itemBinding.cvvTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 2) {
                        checkIfcardDetailfill(itemBinding1, true);
                    } else {
                        checkIfcardDetailfill(itemBinding1, false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            GestureDetector gestureDetector = new GestureDetector(context, new SingleTapConfirm());

            itemBinding.layoutCardInfoShort.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
//                    switch(event.getAction())
//                    {
//                        case MotionEvent.ACTION_DOWN:
//                            itemBinding.layoutCardInfo.setBackgroundColor(context.getResources().getColor(R.color.text_gray7));
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            itemBinding.layoutCardInfo.setBackgroundColor(context.getResources().getColor(R.color.white));
//                            break;
//                    }
                    if (gestureDetector.onTouchEvent(event)) {

                        if (listPaymentMethod.get(position).code.equals("ottu_pg_kwd_tkn")) {
                            selectedCardPos = position;
                            if (itemBinding.layoutCardDetail.getVisibility() == View.GONE) {
                                itemBinding1 = null;
                                itemBinding1 = itemBinding;
                                itemBinding.layoutCardDetail.setVisibility(View.VISIBLE);
//                                setArrow(itemBinding.arrow,true);
                                context.setFee(true,listPaymentMethod.get(position).amount,listPaymentMethod.get(position).currency_code
                                        ,listPaymentMethod.get(position).fee);
                                checkIfcardDetailfill(itemBinding,true);
                            } else {
                                selectedCardPos = -1;
                                itemBinding1 = null;
                                itemBinding.layoutCardDetail.setVisibility(View.GONE);
                                context.setPayEnable(false);
//                                setArrow(itemBinding.arrow,false);
                                context.setFee(false,listPaymentMethod.get(position).amount,listPaymentMethod.get(position).currency_code
                                        ,listPaymentMethod.get(position).fee);
                            }
                        } else if (listPaymentMethod.get(position).code.equals("knet-test")) {
                            if (itemBinding1 != null) {
                                itemBinding1.layoutCardDetail.setVisibility(View.GONE);
                            }
                            itemBinding1 = null;
                            selectedCardPos = position;
                            context.setPayEnable(true);
                            context.setFee(true,listPaymentMethod.get(position).amount,listPaymentMethod.get(position).currency_code
                                    ,listPaymentMethod.get(position).fee);

                        } else if (listPaymentMethod.get(position).code.equals("mpgs")) {
                            if (itemBinding1 != null) {
                                itemBinding1.layoutCardDetail.setVisibility(View.GONE);
                            }
                            itemBinding1 = null;
                            selectedCardPos = position;
                            context.setPayEnable(true);
                            context.setFee(true,listPaymentMethod.get(position).amount,listPaymentMethod.get(position).currency_code
                                    ,listPaymentMethod.get(position).fee);
//                            CreatePaymentTransaction paymentTransaction = new CreatePaymentTransaction(transactionDetail.type
//                                    ,transactionDetail.pg_codes
//                                    ,Amount
//                                    ,listPaymentMethod.get(position).currency_code
//                                    ,transactionDetail.redirect_url.replace("redirected","disclose_ok")
//                                    ,transactionDetail.redirect_url
//                                    ,transactionDetail.customer_id
//                                    ,"1");
//                            context.createTrx(paymentTransaction,transactionDetail.pg_codes.get(position));
                        }
                        notifyDataSetChanged();
                        savedCardSelected = false;
                        context.notifySavedCardAdapter();
                        // single tap
                        return true;
                    }
                    return true;
                }
            });

            itemBinding.infoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context, R.style.MyDialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_savecard);

                    TextView btnClose = (TextView) dialog.findViewById(R.id.btnClose);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            itemBinding.datetextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog monthDatePickerDialog = new DatePickerDialog(context,
                            AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            String months = String.valueOf(month + 1);
                            if (months.length() <= 1) {
                                months = "0" + months;
                            }
                            itemBinding.datetextView.setText(months + "/" + String.valueOf(year).substring(2));
                            checkIfcardDetailfill(itemBinding,true);
                        }
                    }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH) {
                        @Override
                        protected void onCreate(Bundle savedInstanceState) {
                            super.onCreate(savedInstanceState);
                            getDatePicker().findViewById(context.getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                        }
                    };
                    monthDatePickerDialog.setTitle("Select Expiry Date");
                    monthDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

                    monthDatePickerDialog.show();
                }
            });
//            itemBinding.cardNumberTextView.setShowSoftInputOnFocus(false);
//            itemBinding.cardNumberTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
//            itemBinding.cardNumberTextView.setTextIsSelectable(true);
//            context.manageKeyboard(ic,View.VISIBLE);

            InputConnection ic = itemBinding.cardNumberTextView.onCreateInputConnection(new EditorInfo());
            itemBinding.cardNumberTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        itemBinding.cardNumberTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
                        itemBinding.cardNumberTextView.setTextIsSelectable(true);
                        itemBinding.cardNumberTextView.setShowSoftInputOnFocus(false);
                        context.manageKeyboard(ic, View.VISIBLE);
                    }else {
                        context.manageKeyboard(ic, View.GONE);
                    }
                }
            });
            InputConnection ic1 = itemBinding.cvvTextView.onCreateInputConnection(new EditorInfo());
            itemBinding.cvvTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        itemBinding.cvvTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
                        itemBinding.cvvTextView.setTextIsSelectable(true);
                        itemBinding.cvvTextView.setShowSoftInputOnFocus(false);
                        context.manageKeyboard(ic1, View.VISIBLE);
                    }else {
                        context.manageKeyboard(ic1, View.GONE);
                    }
                }
            });
        }

        private void setArrow(ImageView arrow, boolean selected, String pgCode) {
            if (selected){
                if (pgCode.equals("ottu_pg_kwd_tkn")){
                    binding.arrow.setImageResource(R.drawable.arrow_down);
                }
            }else {
                if (LocalLan.equals("ar")) {
                    binding.arrow.setImageResource(R.drawable.arrow_left__24);
                } else {
                    binding.arrow.setImageResource(R.drawable.arrow_right_24);
                }
            }
        }

    }

    private Bitmap getImage(String icon) {
        Bitmap bitmap = null;
        InputStream input = null;
        try {
            input = new URL(icon).openStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public Card_SubmitCHD getCardData() {


        DateFormat month = new SimpleDateFormat("MM");
        DateFormat year = new SimpleDateFormat("yy");
        Date time = new Date();
        Card_SubmitCHD submitCHD = null;

        if (itemBinding1 == null) {
            return submitCHD;
        }

        if (selectedCardPos == -1) {
            return submitCHD;
        } else {

            String cardBrand = listCardName().get(CardListPosition);
//            String name = cardBrand;
            String name = itemBinding1.nameTextView.getText().toString().trim();
            String cardNumber = itemBinding1.cardNumberTextView.getText().toString().trim().replace(" ", "");
            String date = itemBinding1.datetextView.getText().toString().trim();
//            String[] time = date.split("/");

            String cvv = itemBinding1.cvvTextView.getText().toString().trim();
            boolean saveCard = itemBinding1.saveCard.isChecked();

            if (name.equals("") || name.length() < 2){
                itemBinding1.cardNameErrorTextView.setText(context.getResources().getString(R.string.card_name_error));
            }
            if (cardNumber.equals("") || cardNumber.length() < 15) {
                itemBinding1.cardNumberErrorTextView.setText(context.getText(R.string.card_number_not_correct));
                return submitCHD;
            }
            if (date.equals("") || date.length() < 4) {
                itemBinding1.expiredateErrorTextView.setText(context.getText(R.string.expire_data_notbe_past));
                return submitCHD;
            }
            String expiryMonth = date.substring(0, 2);
            String expiryYear = date.substring(3);
            String innput = expiryMonth + "/" + expiryYear;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
            simpleDateFormat.setLenient(false);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, Integer.parseInt(expiryMonth));
            cal.set(Calendar.YEAR, Integer.parseInt("20" + expiryYear));


            boolean expired = cal.before(Calendar.getInstance());
            if (expired) {
                itemBinding1.expiredateErrorTextView.setText(context.getText(R.string.expire_data_notbe_past));
                return submitCHD;
            } else if (Integer.parseInt(String.valueOf(date.charAt(0))) > 1) {
                itemBinding1.expiredateErrorTextView.setText("Month is wrong");
            } else {
                itemBinding1.expiredateErrorTextView.setText("");
            }


            if (cvv.equals("") || cvv.length() < 3) {
                itemBinding1.expiredateErrorTextView.setText("Enter valid cvv");
                return submitCHD;
            }

            submitCHD = new Card_SubmitCHD(name, cardNumber, expiryMonth, expiryYear, cvv, saveCard);
            return submitCHD;
        }
    }


    private String keepNumbersOnly(CharSequence s) {
        return s.toString().replaceAll("[^0-9]", ""); // Should of course be more robust
    }

    private String formatNumbersAsCode(CharSequence s) {
        int groupDigits = 0;
        String tmp = "";
        for (int i = 0; i < s.length(); ++i) {
            tmp += s.charAt(i);
            ++groupDigits;
            if (groupDigits == 4) {
                tmp += " ";
                groupDigits = 0;
            }
        }
        return tmp;
    }

    public static void formatExpiryDate(@NonNull Editable expiryDate, int maxLength) {
        int textLength = expiryDate.length();
        // first remove any previous span
        SlashSpan[] spans = expiryDate.getSpans(0, expiryDate.length(), SlashSpan.class);
        for (int i = 0; i < spans.length; ++i) {
            expiryDate.removeSpan(spans[i]);
        }
        // then truncate to max length
        if (maxLength > 0 && textLength > maxLength - 1) {
            expiryDate.replace(maxLength, textLength, "");
            --textLength;
        }
        // finally add margin spans
        for (int i = 1; i <= ((textLength - 1) / 2); ++i) {
            int end = i * 2 + 1;
            int start = end - 1;
            SlashSpan marginSPan = new SlashSpan();
            expiryDate.setSpan(marginSPan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static class SlashSpan extends ReplacementSpan {

        public SlashSpan() {
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            float[] widths = new float[end - start];
            float[] slashWidth = new float[1];
            paint.getTextWidths(text, start, end, widths);
            paint.getTextWidths("/", slashWidth);
            int sum = (int) slashWidth[0];
            for (int i = 0; i < widths.length; ++i) {
                sum += widths[i];
            }
            return sum;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            String xtext = "/" + text.subSequence(start, end);
            canvas.drawText(xtext, 0, xtext.length(), x, y, paint);
        }
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    private void checkIfcardDetailfill(ItemPaymentMethodBinding itemBinding1, boolean b) {
        boolean cardName,cardenable, dataenable, cvvenable = false;
        if (itemBinding1.nameTextView.getText().toString().trim().length() < 2){
            cardName = false;
        }else {
            cardName = true;
        }
            if (itemBinding1.cardNumberTextView.getText().toString().trim().replace(" ", "").length() > 13) {
            cardenable = true;
        } else {
            cardenable = false;
        }
        if (itemBinding1.datetextView.getText().toString().trim().length() >= 4) {
            dataenable = true;
        } else {
            dataenable = false;
        }
        if (itemBinding1.cvvTextView.getText().toString().trim().length() >= 3) {
            cvvenable = true;
        } else {
            cvvenable = false;
        }

        if (cardName == true &&cardenable == true && dataenable == true && cvvenable == true) {
            context.setPayEnable(true);
        } else {
            context.setPayEnable(false);
        }
    }
}

