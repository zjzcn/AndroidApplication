package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.IFolderItemListener;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.views.OpenFileDialog;
import ru.tcgeo.application.views.adapter.ReLayersAdapter;
import ru.tcgeo.application.views.callback.LayerCallback;
import ru.tcgeo.application.views.callback.LayerHolderCallback;
import ru.tcgeo.application.views.viewholder.LayerHolder;

/**
 * Created by a_belov on 23.07.15.
 */
public class ReSettingsDialog extends Dialog implements IFolderItemListener {

    @Bind(R.id.rvLayers)
    RecyclerView rvLayers;

    ReLayersAdapter adapter;

    private LayerCallback callback;

    private List<GITuple> data;

    private Context context;

    public ReSettingsDialog(Builder builder) {
        super(builder.context);
        this.callback = builder.callback;
        this.context = builder.context;
        this.data = builder.data;
    }

    @OnClick(R.id.fabAdd)
    public void onAddClick() {
        OpenFileDialog dlg = new OpenFileDialog();
        dlg.setIFolderItemListener(ReSettingsDialog.this);
        dlg.show(((FragmentActivity) context).getSupportFragmentManager(), "tag");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_settings);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ReLayersAdapter.Builder(context)
                .callback(new LayerHolderCallback() {
                    @Override
                    public void onMarkersSourceCheckChanged(LayerHolder holder, boolean isChecked) {
                        GITuple tuple = adapter.getItem(holder.getAdapterPosition());
                        callback.onMarkersSourceCheckChanged(tuple, isChecked);
//                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onVisibilityCheckChanged(LayerHolder holder, boolean isChecked) {
                        GITuple tuple = adapter.getItem(holder.getAdapterPosition());
                        callback.onVisibilityCheckChanged(tuple, isChecked);
                    }

                    @Override
                    public void onSettings(LayerHolder holder) {
                        GITuple tuple = adapter.getItem(holder.getAdapterPosition());
                        callback.onSettings(tuple);
                    }
                })
                .data(data)
                .build();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

        rvLayers.setLayoutManager(layoutManager);
        rvLayers.addItemDecoration(dividerItemDecoration);
        rvLayers.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
        int dialogWidth = (int) (ScreenUtils.getScreenWidth(context) * 0.9f);
        int dialogHeight = (int) (ScreenUtils.getScreenHeight(context) * 0.9f);
        getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void OnCannotFileRead(File file) {
        Toast.makeText(context, R.string.file_error,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void OnFileClicked(File file) {
        GITuple result = callback.onAddLayer(file);
        if (result != null) {
            adapter.addItemAt(result);
            adapter.notifyDataSetChanged();
        }
    }

    public static class Builder {

        private LayerCallback callback;
        private List<GITuple> data;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(LayerCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<GITuple> data) {
            this.data = data;
            return this;
        }

        public ReSettingsDialog build() {
            return new ReSettingsDialog(this);
        }
    }
}
