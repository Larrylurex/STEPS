package ru.tomatapps.steps.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ru.tomatapps.steps.database.ContentResolverHelper.SettingsItem;
import ru.tomatapps.steps.dialogs.DialogCreateItem;
import ru.tomatapps.steps.fragments.ListSettings;
import ru.tomatapps.steps.R;


public class ActivitySettings extends AppCompatActivity implements DialogCreateItem.CreateItemDialogListener {
    ListSettings listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(savedInstanceState != null){
            listFragment = (ListSettings)getSupportFragmentManager().findFragmentByTag("ListFragment");
        }
        else {
            listFragment = new ListSettings();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.settingsListContainer, listFragment, "ListFragment");
            trans.commit();
        }
    }

    public void onAddNewItemClick(View view){
        DialogCreateItem dialog = new DialogCreateItem();
        dialog.show(getSupportFragmentManager(), "createDialog");
    }

    @Override
    public void createItem(SettingsItem item) {
        listFragment.addNewItem(item);
    }
}
