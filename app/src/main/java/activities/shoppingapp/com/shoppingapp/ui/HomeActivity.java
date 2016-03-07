package activities.shoppingapp.com.shoppingapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.HashMap;

import activities.shoppingapp.com.shoppingapp.R;
import activities.shoppingapp.com.shoppingapp.adapters.ItemsRecyclerViewAdapter;
import activities.shoppingapp.com.shoppingapp.common.CommonUtils;
import activities.shoppingapp.com.shoppingapp.common.Constant;
import activities.shoppingapp.com.shoppingapp.db.HandbagUtil;
import activities.shoppingapp.com.shoppingapp.models.HandBag;

/**
 * @see android.support.v7.app.AppCompatActivity
 * @author  prerana katyarmal
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ItemsRecyclerViewAdapter mAdapter;
    private HashMap<Integer, HandBag> hashMapOfSelectedItems;
    private ArrayList<HandBag> handbags;
    private SwitchCompat autoSyncSwitch;
    private boolean isAutoSyncOn;
    private boolean isFirstTime;

    public HashMap<Integer, HandBag> getHashMapOfSelectedItems() {
        return hashMapOfSelectedItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.syncSwitch);
        item.setActionView(R.layout.on_off_switch);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inflateXml();
    }

    /**
     * Inflating view and intializing other objects
     */
    private void inflateXml() {
        mRecyclerView = (RecyclerView) findViewById(R.id.items_recyclerview);
        autoSyncSwitch = (SwitchCompat) findViewById(R.id.switchForActionBar);
        isAutoSyncOn = getSharedPreferences(Constant.TAG, MODE_PRIVATE).getBoolean(Constant.AUTO_SYNC_STATUS, false);
        if (isAutoSyncOn) {
            autoSyncSwitch.setChecked(true);
        }
        autoSyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoSyncOn = isChecked;
                getSharedPreferences(Constant.TAG, MODE_PRIVATE).edit().putBoolean(Constant.AUTO_SYNC_STATUS, isAutoSyncOn).commit();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemsRecyclerViewAdapter(getHandBags(), HomeActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<HandBag> bagArrayList = new ArrayList<>();
                bagArrayList.addAll(hashMapOfSelectedItems.values());
                if (isAutoSyncOn) {
                    new addProductToDb().execute(bagArrayList);
                } else {
                    Intent intent = new Intent(HomeActivity.this, ShoppingBasketActivity.class);
                    intent.putExtra(Constant.SELECTED_ITEMS, bagArrayList);
                    startActivity(intent);
                }
            }
        });

        hashMapOfSelectedItems = new HashMap<>();
       if(!isAutoSyncOn && CommonUtils.getIsAppLaunchedFirstTime(this)){
           CommonUtils.setAppLaunchedFirstTime(this, false);
           CommonUtils.showAlert(this,"Shopping","Please Switch On Auto Sync Feature to save basket");
       }
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ItemsRecyclerViewAdapter) mAdapter).setOnItemClickListener(new ItemsRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_checkbox);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    hashMapOfSelectedItems.remove(position + 1);
                } else {
                    checkBox.setChecked(true);
                    hashMapOfSelectedItems.put(position + 1, handbags.get(position));
                }
                Log.v(TAG, hashMapOfSelectedItems.toString());
            }
        });
    }

    /**
     * method to add bags to collection
     *
     * @return handbags
     */
    private ArrayList<HandBag> getHandBags() {
        handbags = new ArrayList<HandBag>();
        handbags.add(new HandBag(1, "Red Bag", R.drawable.red_bag));
        handbags.add(new HandBag(2, "Blue Bag", R.drawable.blue_bag));
        handbags.add(new HandBag(3, "Pink Bag", R.drawable.pink_bag));
        handbags.add(new HandBag(4, "White Bag", R.drawable.white_bag));
        handbags.add(new HandBag(5, "Brown Bag", R.drawable.brown_bag));
        return handbags;
    }

    /**
     * background task to add product to database
     */
    private class addProductToDb extends AsyncTask<ArrayList<HandBag>, Void, Void> {
        ArrayList<HandBag> bagArrayList;

        @Override
        protected Void doInBackground(ArrayList<HandBag>... params) {
            bagArrayList = params[0];
            for (int i = 0; i < bagArrayList.size(); i++) {
                HandbagUtil.addProductToDataBase(bagArrayList.get(i), HomeActivity.this);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(HomeActivity.this, ShoppingBasketActivity.class);
            intent.putExtra(Constant.SELECTED_ITEMS, bagArrayList);
            startActivity(intent);
        }
    }
}

