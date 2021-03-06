package com.greylabs.sumod.dbct10.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.R;


public class CategoryList extends AppCompatActivity {
    DBHandler db;
    TextView category_name;
    public void gotoCategoryAdd(View view){
        Intent i = new Intent(this, CategoryAdd.class);
        startActivity(i);
    }

    public void buttonRefreshCategoryList(View view){
        populateListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        db = new DBHandler(this, null, null, 1);
        populateListView();
    }

    @Override
    protected void onResume() {
        populateListView();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void populateListView(){
        Cursor cursor = db.getAllCategories();

        String[] fromFieldNames = new String[] {"_id", DBHandler.KEY_DESCRIPTION};

        int[] toViewIDs = new int[] {R.id.category_name, R.id.category_description};

        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.category_list_item, cursor, fromFieldNames, toViewIDs, 0);
        ListView categorylistview = (ListView) findViewById(R.id.categorylistview);

        categorylistview.setAdapter(myCursorAdapter);
        categorylistview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        category_name = (TextView) view.findViewById(R.id.category_name);
                        String categoryname = category_name.getText().toString();
                        Intent objIntent = new Intent(getApplicationContext(),CategoryDetails.class);
                        objIntent.putExtra("category_name", categoryname);
                        startActivity(objIntent);
                    }
                }
        );
    }

    /*
    public void populateListView(){
        try {
            ArrayList<HashMap<String, String>> categoryList = db.getCategoryList(this);
            ListView categorylistview = (ListView) findViewById(R.id.categorylistview);
            ListAdapter adapter = new SimpleAdapter(CategoryList.this, categoryList, R.layout.category_list_item,
                    new String[]{"name", "description"},
                    new int[]{R.id.category_name, R.id.category_description});
            categorylistview.setAdapter(adapter);
            categorylistview.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            category_name = (TextView) view.findViewById(R.id.category_name);
                            String categoryname = category_name.getText().toString();
                            Intent objIntent = new Intent(getApplicationContext(),CategoryDetails.class);
                            objIntent.putExtra("category_name", categoryname);
                            startActivity(objIntent);
                        }
                    }
            );
        }
        catch(SQLiteException e){
            ShowAlert("Exception Caught:", e.getMessage());
        }
        catch(SQLException e){
            ShowAlert("Exception Caught", e.getMessage());
        }
    }
    */

    public void ShowAlert(String title, String message){
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // here you can add functions
            }
        });
        alertDialog.setIcon(R.drawable.abc_dialog_material_background_dark);
        alertDialog.show();
    }
}
