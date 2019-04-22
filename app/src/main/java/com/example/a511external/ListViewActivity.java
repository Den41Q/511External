package com.example.a511external;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ListViewActivity extends AppCompatActivity {

    private List<Map<String, String>> simpleAdapterContent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        final ListView list = findViewById(R.id.list);
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swiperefresh);
        final Button btnAdd = findViewById(R.id.btn_add_string);
        setSupportActionBar(toolbar);

        prepareContent();

        final SimpleAdapter listContentAdapter = createAdapter(simpleAdapterContent);

        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                simpleAdapterContent.remove(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareContent();
                listContentAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = randomString();
                String[] b = randomString().split("");
                saveText(a);
            }
        });


    }

    @NonNull
    private SimpleAdapter createAdapter(List<Map<String, String>> data) {
        return new SimpleAdapter(this, data, R.layout.simple_list_item,
                new String[]{"text_1", "text_2"}, new int[]{R.id.text_1, R.id.text_2});

    }

    @NonNull
    private void prepareContent() {
        if (loadText() == null) {

            String[] arrayContent = getString(R.string.large_text).split("\n\n");

            String content = getString(R.string.large_text);
            saveText(content);

            addList(arrayContent);
        } else {
            simpleAdapterContent.clear();
            String content = loadText();
            String[] arrayContent = content.split("\n\n");

            addList(arrayContent);
        }
    }

    private String randomString() {
        String symbols = "qwerty";
        StringBuilder randString = new StringBuilder();
        int count = (int)(Math.random()*30);
        for(int i=0;i<count;i++)
            randString.append(symbols.charAt((int)(Math.random()*symbols.length())));

        return new String(randString);
    }

    private void addList(String[] arrayContent) {
        for (String array : arrayContent) {
            Map<String, String> map = new HashMap<>();
            map.put("text_1", array);
            map.put("text_2", Integer.toString(array.length()));
            simpleAdapterContent.add(map);
        }
    }

    private void saveText(String note) {

        try (Writer writer = new FileWriter(getNoteFile())) {
            if (loadText() != null) {
                writer.append(note);
                writer.flush();
            } else {
                writer.write(note);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Не возможно сохранить файл!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String loadText() {
        File noteFile = getNoteFile();
        if (!noteFile.canRead()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(new FileReader(noteFile))) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private File getNoteFolder() {
        return getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    }

    private File getNoteFile() {
        return new File(getNoteFolder(), "note.txt");
    }
}