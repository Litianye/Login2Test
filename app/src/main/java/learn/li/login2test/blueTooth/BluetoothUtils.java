package learn.li.login2test.blueTooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 李天烨 on 2016/10/19.
 */
public class BluetoothUtils {

    //与设备配对
    public static boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    //与设备解除配对
    public static boolean removeBond(Class<?> btClass, BluetoothDevice btDevice)
            throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    public static boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice,
                                 String str) throws Exception {
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",
                    new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                    new Object[]{str.getBytes()});
            Log.e("returnValue", ""+returnValue);
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    //取消用户输入
    public static boolean cancelPairingUserInput(Class<?> btClass,BluetoothDevice device)
            throws Exception{
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    //取消配对
    public static boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device)
            throws Exception{
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    //确认配对
    public static void setPairingConfirmation(Class<?> btClass, BluetoothDevice device,
                                              boolean isConfirm) throws Exception{
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
    }

    public static void printAllInfom(Class clsShow){
        try{//取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i=0;
            for (; i<hideMethod.length; i++){
                Log.e("Method name", hideMethod[i].getName()+";and i is:"+i);
            }
            //取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i=0; i<allFields.length; i++){
                Log.e("Field name", allFields[i].getName());
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}