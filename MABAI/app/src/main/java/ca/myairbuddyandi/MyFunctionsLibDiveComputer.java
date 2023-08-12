package ca.myairbuddyandi;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Michel on 2023-04-12.
 * Holds all of the logic for the MyFunctionsLibDivComputer class
 */

public final class MyFunctionsLibDiveComputer {

    // Static
    private static final String LOG_TAG = "MyFunctionsLibDiveComputer";
    static ArrayList<LibDiveBleComputer> libDiveBleComputerList = new ArrayList<>();

    // Public

    // Protected

    // Private

    // End of variables

    // Public constructor
    public MyFunctionsLibDiveComputer() {
    }

    // Static method that loads a shared library from the file system into memory
    // and makes its exported functions available for our Java code
    static {System.loadLibrary("myairbuddyandi");}

    // ***** Call to libdivecomputer functions *****

    // NOTE: Do not return a Long
    //       It requires a jobject and it does NOT work
    public static native LibDiveComputerReturnData customOpen(MyFunctionsBle myFunctionsBle);

    public static native LibDiveComputerReturnData deviceForeach(long iostream, long device, String deviceName, String lastDiveFingerprint);

    public static native LibDiveComputerReturnData deviceOpen(String vendor, String product, String devname, long iostream);

    public static native LibDiveComputerReturnData download(MyFunctionsBle myFunctionsBle, String vendor, String product, String deviceName, String lastDiveFingerprint);

    public static native LibDiveComputer[] getSupportedDiveComputers(int transport);

    public static native LibDiveComputer[] getSupportedDiveComputerPerVendor(int transport, String vendor);

    public static native String[] getSupportedProductsPerVendor(int transport, String vendor);

    public static native String[] getSupportedVendors(int transport);

    public static native LibDiveComputerReturnData iostreamClose(long iostream);

    public static native void setCancel(int cancel);

    // TODO: To be removed
    public static native void testGetMethod(MyFunctionsBle myFunctionsBle, int timeOut);

    // ***** Local functions to support libdivecomputer *****

    public static void initLibDiveBleComputerList(Context context) {
        String[] descriptions;
        String[] services;
        String[] characteristics;
        descriptions = context.getResources().getStringArray(R.array.libdivecomputer_vendor_arrays);
        services = context.getResources().getStringArray(R.array.libdivecomputer_service_arrays);
        characteristics = context.getResources().getStringArray(R.array.libdivecomputer_characteristic_arrays);
        for (int i = 0; i < descriptions.length; i++) {
            LibDiveBleComputer libDiveBleComputer = new LibDiveBleComputer();
            libDiveBleComputer.setVendor(descriptions[i]);
            libDiveBleComputer.setService(services[i]);
            libDiveBleComputer.setCharacteristic(characteristics[i]); // Write
            libDiveBleComputerList.add(libDiveBleComputer);
            Log.d(LOG_TAG, "initLibDiveComputer " + libDiveBleComputer.getVendor() + " Service "
                    + libDiveBleComputer.getService()
                    + " Characteristic " + libDiveBleComputer.getCharacteristic());
        }
    }

    public static LibDiveComputerFound isLibDiveComputer(String service) {
        LibDiveComputerFound libDiveComputerFound = new LibDiveComputerFound();
        for (int i=0;i<libDiveBleComputerList.size();i++)
        {
            LibDiveBleComputer libDiveBleComputer = libDiveBleComputerList.get(i);
            if (service.equalsIgnoreCase(libDiveBleComputer.getService())) {
                libDiveComputerFound.setFound(true);
                libDiveComputerFound.setVendor(libDiveBleComputer.getVendor());
                libDiveComputerFound.setService(libDiveBleComputer.getService());
                libDiveComputerFound.setCharacteristicRx(libDiveBleComputer.getCharacteristic());
                libDiveBleComputer = libDiveBleComputerList.get(i+1);
                libDiveComputerFound.setCharacteristicTx(libDiveBleComputer.getCharacteristic());
                libDiveBleComputer = libDiveBleComputerList.get(i+2);
                libDiveComputerFound.setCharacteristicRxCredits(libDiveBleComputer.getCharacteristic());
                libDiveBleComputer = libDiveBleComputerList.get(i+3);
                libDiveComputerFound.setCharacteristicTxCredits(libDiveBleComputer.getCharacteristic());
                return libDiveComputerFound;
            }
        }
        return libDiveComputerFound;
    }
}
