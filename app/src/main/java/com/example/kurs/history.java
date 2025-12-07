package com.example.kurs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class history extends AppCompatActivity {

    private ListView listView;
    private Button btnClearAll, btnBack;
    private TextView tvEmpty;
    private DB databaseHelper;
    private List<Transcription> transcriptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        btnClearAll = findViewById(R.id.btnClearAll);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        databaseHelper = new DB(this);

        loadTranscriptions();

        // Обработчик нажатия на элемент списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showTranscriptionDialog(position);
            }
        });

        // Обработчик долгого нажатия на элемент списка (для удаления)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteTranscriptionDialog(position);
                return true;
            }
        });

        // Кнопка очистки всей истории
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllDialog();
            }
        });

        // Кнопка возврата
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadTranscriptions() {
        transcriptionList = databaseHelper.getAllTranscriptions();

        if (transcriptionList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            // Создаем массив строк для отображения
            String[] items = new String[transcriptionList.size()];
            for (int i = 0; i < transcriptionList.size(); i++) {
                Transcription t = transcriptionList.get(i);
                String text = t.getText();
                if (text.length() > 50) {
                    text = text.substring(0, 50) + "...";
                }
                items[i] = t.getTimestamp() + "\n" + text;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);
        }
    }

    private void showTranscriptionDialog(int position) {
        Transcription transcription = transcriptionList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Запись от " + transcription.getTimestamp());
        builder.setMessage(transcription.getText());
        builder.setPositiveButton("Закрыть", null);
        builder.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTranscription(position);
            }
        });
        builder.show();
    }

    private void deleteTranscriptionDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить запись")
                .setMessage("Вы уверены, что хотите удалить эту запись?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTranscription(position);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteTranscription(int position) {
        Transcription transcription = transcriptionList.get(position);
        databaseHelper.deleteTranscription(transcription.getId());
        transcriptionList.remove(position);
        loadTranscriptions();
        Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show();
    }

    private void clearAllDialog() {
        if (transcriptionList.isEmpty()) {
            Toast.makeText(this, "История уже пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Очистить историю")
                .setMessage("Вы уверены, что хотите удалить все записи?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteAllTranscriptions();
                        transcriptionList.clear();
                        loadTranscriptions();
                        Toast.makeText(history.this, "История очищена", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTranscriptions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}