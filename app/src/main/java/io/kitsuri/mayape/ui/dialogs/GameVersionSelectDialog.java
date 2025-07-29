package io.kitsuri.mayape.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.kitsuri.mayape.R;
import io.kitsuri.mayape.core.versions.GameVersion;
import io.kitsuri.mayape.ui.dialogs.gameversionselect.BigGroup;
import io.kitsuri.mayape.ui.dialogs.gameversionselect.UltimateVersionAdapter;

import java.util.List;

public class GameVersionSelectDialog extends Dialog {
    public interface OnVersionSelectListener {
        void onVersionSelected(GameVersion version);
    }

    private OnVersionSelectListener listener;
    private List<BigGroup> bigGroups;

    public GameVersionSelectDialog(@NonNull Context ctx, List<BigGroup> bigGroups) {
        super(ctx);
        this.bigGroups = bigGroups;
    }

    public void setOnVersionSelectListener(OnVersionSelectListener l) {
        this.listener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_version_select);
        RecyclerView recyclerView = findViewById(R.id.recycler_versions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        UltimateVersionAdapter adapter = new UltimateVersionAdapter(getContext(), bigGroups);
        adapter.setOnVersionSelectListener(v -> {
            if (listener != null) listener.onVersionSelected(v);
            dismiss();
        });
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        recyclerView.setAdapter(adapter);
    }
}