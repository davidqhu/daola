package com.who.daola;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.who.daola.data.Fence;
import com.who.daola.gcm.GcmHelper;

import java.util.ArrayList;

/**
 * Created by hud on 3/29/15.
 */
public class ShareIdFragment extends Fragment {

    public static ShareIdFragment newInstance() {
        ShareIdFragment fragment = new ShareIdFragment();
        return fragment;
    }

    public ShareIdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_id, container, false);

        ImageButton mButton = (ImageButton) view.findViewById(R.id.image_button_qrcode);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showErrorDialog(3);
            }
        });

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(
                    NfcHelper.getMsgContent(), BarcodeFormat.QR_CODE, 600, 600
            );
            mButton.setImageBitmap(toBitmap(matrix));
            // Now what??
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void onShareViaNFCClicked(View view){

    }

    public void onShareViaQrCodeClicked(View view){
        showErrorDialog(3);
    }

    private void showErrorDialog(int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_connection_failed_message + " Got error code: " + errorCode)
                .setTitle(R.string.dialog_connection_failed_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Writes the given Matrix on a new Bitmap object.
     * @param matrix the matrix to write.
     * @return the new {@link Bitmap}-object.
     */
    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
