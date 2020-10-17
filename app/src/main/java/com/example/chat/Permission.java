package com.example.chat;

import android.app.Activity;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

   private String[] getPermissionList(){
        List<String> permissionList =new ArrayList<>();
        for (EnumContainer.permissions permission: EnumContainer.permissions.values()) {
            permissionList.add(permission.Value);
        }

        String[] permissionArray=new String[permissionList.size()];
        for(int i=0;i<permissionList.size();i++){
            permissionArray[i]= permissionList.get(i);
        }
        return permissionArray;
    }
   public  void askPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                getPermissionList(),
                EnumContainer.RequestCode.ASK_FOR_PERMISSION.Value);
    }

}
