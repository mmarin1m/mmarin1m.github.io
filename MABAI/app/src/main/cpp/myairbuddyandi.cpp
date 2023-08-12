// Write C++ code here.
// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("myairbuddyandi");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("myairbuddyandi")
//      }
//    }
//
// Or, in MyFunctionsLibDiveComputer.java:
//
// public MyFunctionsLibDiveComputer() {
// }
// Static method that loads a shared library from the file system into memory
// and makes its exported functions available for our Java code
//static {System.loadLibrary("myairbuddyandi");}

/**
 * Created by Michel on 2023-05-05
 * Holds all of the logic for the myairbuddyandi.cpp class
 *
 * This class is the glue code between MABAI java code and libdivecomputer C code
 */

#include <jni.h>
#include <cstdlib>
#include <cstring>

#include <android/log.h>

#include <libdivecomputer/context.h>
#include <libdivecomputer/iterator.h>
#include <libdivecomputer/descriptor.h>
#include <libdivecomputer/bluetooth.h>
#include <libdivecomputer/custom.h>
#include <libdivecomputer/iostream.h>
#include <libdivecomputer/device.h>

#include "libdivecomputer/src/iostream-private.h"

#define  LOG_TAG    "myairbuddyandi.cpp"
#define  ALOG(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

// *****
// ***** Structures and types *****
// *****

typedef struct dctool_output_vtable_t dctool_output_vtable_t;

struct dctool_output_t {
    const dctool_output_vtable_t *vtable;
    unsigned int number;
};

typedef struct dive_data_t {
    dc_device_t *device;
    dc_buffer_t **fingerprint;
    unsigned int number;
    dctool_output_t *output;
} dive_data_t;

typedef struct event_data_t {
    const char *cachedir;
    dc_event_devinfo_t devinfo;
} event_data_t;

// Later declared as mabai
// Passed in as input argument to dc_custom_open()
// And referred to as *userdata by libdivecomputer
typedef struct mabai_t {
    // Kept here because all of mabai_set_xxx need it??
    dc_iostream_t *iostream;
    // Kept here because some of mabai_set_xxx need it??
    size_t nRead, nWrite;
    // MABAI variables
    // JNIEnv environment pointer
    JNIEnv  *env;
    // Represent the instance of MyFunctionsBle passed from ComputerDiveActivity.instantiateLdcFunctions()
    jobject myBleCallback;
} mabai_t;

// *****
// ***** Global variables *****
// *****

jint mCancel; // 0 = continue 1 = cancel

// *****
// ***** Local variables *****
// *****

// If the user wants to cancel the download or not
//jint cancel = 0;
jlong failureL = -1;
jlong notApplicableL = 0;
jlong successL = 1;
jobject returnObject;

// *****
// ***** Local functions *****
// *****

const char *
getStatus1 (dc_status_t status) {
    switch (status) {
        case DC_STATUS_SUCCESS:
            return "Success";
        case DC_STATUS_CANCELLED:
            return "Cancelled";
        case DC_STATUS_DATAFORMAT:
            return "Data format error";
        case DC_STATUS_INVALIDARGS:
            return "Invalid arguments";
        case DC_STATUS_IO:
            return "Input/output error";
        case DC_STATUS_NOACCESS:
            return "Access denied";
        case DC_STATUS_NODEVICE:
            return "No device found";
        case DC_STATUS_NOMEMORY:
            return "Out of memory";
        case DC_STATUS_PROTOCOL:
            return "Protocol error";
        case DC_STATUS_TIMEOUT:
            return "Timeout";
        case DC_STATUS_UNSUPPORTED:
            return "Unsupported operation";
        default:
            return "Unknown error";
    }
}

jstring
getStatus2 (JNIEnv*  env, dc_status_t status) {
    switch (status) {
        case DC_STATUS_SUCCESS:
            return env->NewStringUTF("Success");
        case DC_STATUS_CANCELLED:
            return env->NewStringUTF("Cancelled");
        case DC_STATUS_DATAFORMAT:
            return env->NewStringUTF("Data format error");
        case DC_STATUS_INVALIDARGS:
            return env->NewStringUTF("Invalid arguments");
        case DC_STATUS_IO:
            return env->NewStringUTF("Input/output error");
        case DC_STATUS_NOACCESS:
            return env->NewStringUTF("Access denied");
        case DC_STATUS_NODEVICE:
            return env->NewStringUTF("No device found");
        case DC_STATUS_NOMEMORY:
            return env->NewStringUTF("Out of memory");
        case DC_STATUS_PROTOCOL:
            return env->NewStringUTF("Protocol error");
        case DC_STATUS_TIMEOUT:
            return env->NewStringUTF("Timeout");
        case DC_STATUS_UNSUPPORTED:
            return env->NewStringUTF("Unsupported operation");
        default:
            return env->NewStringUTF("Unknown error");
    }
}

jobject getReturnDataObject (JNIEnv  *env, dc_status_t status, jlong iostreamData, jlong deviceData, jlong diveData) {
    jclass returnObjectClass = env->FindClass("ca/myairbuddyandi/LibDiveComputerReturnData");
    jobject dataObject = env->AllocObject(returnObjectClass);
    // Get all the fields, just in case
    jfieldID rcStatus = env->GetFieldID(returnObjectClass , "status", "Ljava/lang/String;");
    jfieldID rcIostream = env->GetFieldID(returnObjectClass , "iostream", "J");
    jfieldID rcDevice = env->GetFieldID(returnObjectClass , "device", "J");
    jfieldID rcDiveData = env->GetFieldID(returnObjectClass , "diveData", "J");

    // All the fields will be initialized, just in case
    env->SetObjectField(dataObject, rcStatus, (jobject) getStatus2(env, status));
    env->SetLongField(dataObject, rcIostream, iostreamData);
    env->SetLongField(dataObject, rcDevice, deviceData);
    env->SetLongField(dataObject, rcDiveData, diveData);

    return dataObject;
}

static unsigned char
hex2dec (unsigned char value) {
    if (value >= '0' && value <= '9')
        return value - '0';
    else if (value >= 'A' && value <= 'F')
        return value - 'A' + 10;
    else if (value >= 'a' && value <= 'f')
        return value - 'a' + 10;
    else
        return 0;
}

dc_buffer_t *
dctool_convert_hex2bin (const char *str) {
    // Get the length of the fingerprint data.
    size_t nbytes = (str ? strlen (str) / 2 : 0);
    if (nbytes == 0)
        return nullptr;

    // Allocate a memory buffer.
    dc_buffer_t *buffer = dc_buffer_new (nbytes);

    // Convert the hexadecimal string.
    for (unsigned int i = 0; i < nbytes; ++i) {
        unsigned char msn = hex2dec (str[i * 2 + 0]);
        unsigned char lsn = hex2dec (str[i * 2 + 1]);
        unsigned char byte = (msn << 4) + lsn;

        dc_buffer_append (buffer, &byte, 1);
    }

    return buffer;
}

// *****
// ***** Callback functions from deviceForeach() *****
// *****

//void
//dctool_event_cb (dc_device_t *device, dc_event_type_t event, const void *data, void *userdata) {
//    ALOG("dctool_event_cb.\n");
//    const auto *progress = (const dc_event_progress_t *) data;
//    const auto *devinfo = (const dc_event_devinfo_t *) data;
//    const auto *clock = (const dc_event_clock_t *) data;
//    const auto *vendor = (const dc_event_vendor_t *) data;
//
//    switch (event) {
//        case DC_EVENT_WAITING:
//            ALOG("dctool_event_cb. Event: waiting for user action\n");
//            break;
//        case DC_EVENT_PROGRESS:
//            ALOG("dctool_event_cb. Event: progress %3.2f%% (%u/%u)\n",
//                 100.0 * (double) progress->current / (double) progress->maximum,
//                 progress->current, progress->maximum);
//            break;
//        case DC_EVENT_DEVINFO:
//            ALOG ("dctool_event_cb. Event: devinfo model=%u (0x%08x), firmware=%u (0x%08x), serial=%u (0x%08x)\n",
//                     devinfo->model, devinfo->model,
//                     devinfo->firmware, devinfo->firmware,
//                     devinfo->serial, devinfo->serial);
//            break;
//        case DC_EVENT_CLOCK:
//            ALOG ("dctool_event_cb. Event: clock sys-time = %lld, dev-time = %u\n",  clock->systime, clock->devtime);
//            break;
//        case DC_EVENT_VENDOR:
//            ALOG("dctool_event_cb. Event: vendor=");
//            for (unsigned int i = 0; i < vendor->size; ++i)
//                ALOG("%02X", vendor->data[i]);
//            ALOG("\n");
//            break;
//        default:
//            break;
//    }
//}

// Called back for every dive downloaded from the DC
// Dive data:
// - *data
// - size
// fingerprint data:
// - *fingerprint
// - fsize
// *userdata contain the same value passed as the last argument to the dc_device_foreach() which is always nullptr
static dc_status_t
dive_cb (const unsigned char *data, unsigned int size, const unsigned char *fingerprint, unsigned int fsize, void *userdata) {
    ALOG("dive_cb. Received dive (%u bytes).", size);

    // NOTE: Return 0 to stop the download
    // TODO: Need to store the dive data and fingerprint somewhere/somehow
    return DC_STATUS_SUCCESS;
}

static void
event_cb (dc_device_t *device, dc_event_type_t event, const void *data, void *userdata) {
    ALOG("event_cb.\n");
    const auto *devinfo = (const dc_event_devinfo_t *) data;

    auto *eventdata = (event_data_t *) userdata;

    // Forward to the default event handler.
    // TODO: Do I need it??
//    dctool_event_cb (device, event, data, userdata);

    switch (event) {
        case DC_EVENT_DEVINFO:
            // Load the fingerprint from the cache. If there is no
            // fingerprint present in the cache, a NULL buffer is returned,
            // and the registered fingerprint will be cleared.

            // TODO: Debug this callback!!
            //       Might be only for dctool
            //       Make sure dive_cb brings back the fingerprint
            //       Check on how to get the serial number out of it!
//            if (eventdata->cachedir) {
//                char filename[1024] = {0};
//                dc_family_t family = DC_FAMILY_NULL;
//                dc_buffer_t *fingerprint = NULL;
//
//                // Generate the fingerprint filename.
//                family = dc_device_get_type (device);
//
//
//                snprintf (filename, sizeof (filename), "%s/%s-%08X.bin",
//                          eventdata->cachedir, dctool_family_name (family), devinfo->serial);
//
//                // Read the fingerprint file.
//                fingerprint = dctool_file_read (filename);
//
//                // Register the fingerprint data.
//                dc_device_set_fingerprint (device,
//                                           dc_buffer_get_data (fingerprint),
//                                           dc_buffer_get_size (fingerprint));
//
//                // Free the buffer again.
//                dc_buffer_free (fingerprint);
//            }

            // Keep a copy of the event data. It will be used for generating
            // the fingerprint filename again after a (successful) download.
            eventdata->devinfo = *devinfo;
            break;
        default:
            break;
    }
}

int
cancel_cb (void *userdata) {
    ALOG("cancel_cb.\n");

    // NOTE: Return 0 for continue
    //              1 to cancel/stop
    // TODO: Test return variable
//    auto cancelL = reinterpret_cast<jlong>(mCancel);
//    jint cancelI = cancelL;
//    return cancelI;
    return mCancel;
}

// *****
// ***** Callback functions from "static const dc_custom_cbs_t mabai_cbs = {...}" *****
// *****

// Cleanup any resource associated with your iostream implementation
// And cleanup in MABAI. e.g. Reset on DC to terminate the Bluetooth connection on the DC
// *userData is passed by reference and is of type mabai_t
static dc_status_t
mabai_close (void *userData) {
    ALOG("mabai_close.\n");

    auto *mabai = (mabai_t *) userData;

    // Represent the MyFunctionsBle.close() function
    jclass userObjectClass = mabai->env->GetObjectClass(mabai->myBleCallback);
    jmethodID methodId = mabai->env->GetMethodID(userObjectClass, "close", "()J");
    jobject myObject = mabai->myBleCallback;

    // Return SUCCESS if no method exists
    // No method means no implementation, means not required
    if (methodId == nullptr)
        return DC_STATUS_SUCCESS;

    jlong status = mabai->env->CallLongMethod(myObject, methodId);

    // Free all resources you allocated in the mabai_open() function
    mabai->env->DeleteGlobalRef(mabai->myBleCallback);
    free(mabai);

    // Call the garbage Collector
    jclass    systemClass    = nullptr;
    jmethodID systemGCMethod = nullptr;
    systemClass    = mabai->env->FindClass("java/lang/System");
    systemGCMethod = mabai->env->GetStaticMethodID(systemClass, "gc", "()V");
    mabai->env->CallStaticVoidMethod(systemClass, systemGCMethod);

    if (status == 1) {
        return DC_STATUS_SUCCESS;
    } else {
        return DC_STATUS_IO;
    }
}

// *userData is passed by reference and is of type mabai_t
// baudRate is passed by value and is of type int
// dataBits is passed by value and is of type int
// parity is passed by value and is of type int
// stopBits is passed by value and is of type int
// flowControl is passed by value and is of type int
static dc_status_t
mabai_configure (void *userData, unsigned int baudRate, unsigned int dataBits, dc_parity_t parity, dc_stopbits_t stopBits, dc_flowcontrol_t flowControl) {
    ALOG("mabai_configure.\n");

    // Required only for serial communication and not needed for BLE

    return DC_STATUS_SUCCESS;
}

// Send out any buffered output data
// *userData is passed by reference and is of type mabai_t
static dc_status_t
mabai_flush (void *userData) {
    ALOG("mabai_flush.\n");

    // Not needed for BLE because we shouldn't buffer packets (when sending)

    return DC_STATUS_SUCCESS;
}

// *userData is passed by reference and is of type mabai_t
// *value is passed by reference and is of type long
static dc_status_t
mabai_get_available (void *userData, size_t *value) {
    ALOG("mabai_get_available.\n");

    // Required only for serial communication and not needed for BLE

    return DC_STATUS_SUCCESS;
}

// *userData is passed by reference and is of type mabai_t
// *value is passed by value and is of type long (unsigned int)
static dc_status_t
mabai_get_lines (void *userData, unsigned int *value) {
    ALOG("mabai_get_lines.\n");

    // Required only for serial communication and not needed for BLE

    return DC_STATUS_SUCCESS;
}

// Perform some advanced operation not covered by any of the other function
// *userData is passed by reference and is of type mabai_t
// request is passed by value and is of type long (unsigned int)
// *data is passed by reference and is of type byte[]??
// size is passed by value and is of type int
// TODO: Convert data
// TODO: Test all arguments
static dc_status_t
mabai_ioctl (void *userData, unsigned int request, void *data, size_t size) {
    ALOG("mabai_ioctl.\n");

    auto *mabai = (mabai_t *) userData;

    // Not needed for now

    return DC_STATUS_SUCCESS;
}

// Check for a packet without retrieving it
// *userData is passed by reference and is of type mabai_t
// timeout is passed by value and is of type int
static dc_status_t
mabai_poll (void *userData, int timeout) {
    ALOG("mabai_poll.\n");

    auto *mabai = (mabai_t *) userData;

    // Represent the MyFunctionsBle.poll() function
    jclass userObjectClass = mabai->env->GetObjectClass(mabai->myBleCallback);
    jmethodID methodId = mabai->env->GetMethodID(userObjectClass, "poll", "()J");
    jobject myObject = mabai->myBleCallback;

    // Return SUCCESS if no method exists
    // No method means no implementation, means not required
    if (methodId == nullptr)
        return DC_STATUS_SUCCESS;

    jlong status = mabai->env->CallLongMethod(myObject, methodId, timeout);

    if (status == 1) {
        return DC_STATUS_SUCCESS;
    } else {
        return DC_STATUS_IO;
    }
}

// Discard any buffered packets (input and/or output)
// Mainly used when recovering from errors.
// *userData is passed by reference and is of type mabai_t
// direction is passed by value and is of type int
static dc_status_t
mabai_purge (void *userData, dc_direction_t direction) {
    ALOG("mabai_purge.\n");

    auto *mabai = (mabai_t *) userData;

    // Not needed for now

    return DC_STATUS_SUCCESS;
}

// Read the next packet from the queue
// *userData is passed by reference and is of type mabai_t
// *data is passed by reference and is of type byte[]??
// size is passed by value and is of type int
// *actual is passed by reference and is of type long
// TODO: Convert data??
extern "C" {
    static dc_status_t
    mabai_read(void *userData, void *data, size_t size, size_t *actual) {
        ALOG("mabai_read. Size: %d, Actual: %u\n", size, *actual);

        auto *mabai = (mabai_t *) userData;

        // TODO: Why do we need to increment nRead here? Or do we need it at all?
        mabai->nRead += size;

        jclass userObjectClass = mabai->env->GetObjectClass(mabai->myBleCallback);
        jmethodID methodId = mabai->env->GetMethodID(userObjectClass, "read", "()[B");
        jobject myObject = mabai->myBleCallback;

        // Return SUCCESS if no method exists
        // No method means no implementation, means not required
        if (methodId == nullptr)
            return DC_STATUS_SUCCESS;

        // Get the data from the mPacketQueue
        jbyteArray byteArray = (jbyteArray)mabai->env->CallObjectMethod(myObject, methodId);

        if (byteArray == nullptr) {
            ALOG("mabai_read. byteArray is NULL. Returning DC_STATUS_TIMEOUT\n");
            return DC_STATUS_TIMEOUT;
        }

        // TODO: Not sure how to get the byte array?
        auto array = static_cast<jbyteArray>(byteArray);

        /* Get the pointer and length. */
        jboolean isCopy;
        // Crashing on this line
        jint len = mabai->env->GetArrayLength(array);
        jbyte *buf = mabai->env->GetByteArrayElements(array, &isCopy);

        /* Copy the data. */
        if (len > size) {
            /* Packet is too large. Copy the first size bytes only. */
            memcpy(data, buf, size);
            *actual = size;
        } else {
            memcpy(data, buf, len);
            *actual = len;
        }

        /* Release the pointer. */
        mabai->env->ReleaseByteArrayElements(array, buf, 0);

        if (len > size) {
            /* Packet is too large. Return error. */
            return DC_STATUS_IO;
        } else {
            return DC_STATUS_SUCCESS;
        }
    }
}

// *userData is passed by reference and is of type mabai_t
// value is passed by value and is of type long (unsigned int)
static dc_status_t
mabai_set_break (void *userData, unsigned int value) {
    ALOG("mabai_set_break.\n");

    // Required only for serial communication and not needed for BLE

    return DC_STATUS_SUCCESS;
}

// *userData is passed by reference and is of type mabai_t
// value is passed by value and is of type long (unsigned int)
static dc_status_t
mabai_set_dtr (void *userData, unsigned int value) {
    ALOG("mabai_set_dtr.\n");

    // Required only for serial communication and not needed for BLE

    return DC_STATUS_SUCCESS;
}

// *userData is passed by reference and is of type mabai_t
// value is passed by value and is of type long (unsigned int)
extern "C" {
    static dc_status_t
    mabai_set_rts(void *userData, unsigned int value) {
        ALOG("mabai_set_rts.\n");

        // Required only for serial communication and not needed for BLE

        return DC_STATUS_SUCCESS;
    }
}

// Set the 1 to avoid runaway read or write
// *userData is passed by reference and is of type mabai_t
// timeout is passed by value and is of type int
extern "C" {
    static dc_status_t
    mabai_set_timeout (void *userData, int timeout) {
        ALOG("mabai_set_timeout.\n");

        auto *mabai = (mabai_t *) userData;

        // Represent the MyFunctionsBle.setTimeout() function
        jclass userObjectClass = mabai->env->GetObjectClass(mabai->myBleCallback);
        jmethodID methodId = mabai->env->GetMethodID(userObjectClass, "setTimeout", "(I)J");
        jobject myObject = mabai->myBleCallback;

        // Return SUCCESS if no method exists
        // No method means no implementation, means not required
        if (methodId == nullptr)
            return DC_STATUS_SUCCESS;

        jlong status = mabai->env->CallLongMethod(myObject, methodId, timeout);

        if (status == 1) {
            return DC_STATUS_SUCCESS;
        } else {
            return DC_STATUS_IO;
        }
    //    return DC_STATUS_SUCCESS;
    }
}

// Wait and do nothing for the specified amount
// Used when the dive computer can't handle sending commands too fast
// *userData is passed by reference and is of type mabai_t
// milliseconds is passed by value and is of type long (unsigned int)
extern "C" {
    static dc_status_t
    mabai_sleep(void *userData, unsigned int milliseconds) {
        ALOG("mabai_sleep.\n");

        auto *mabai = (mabai_t *) userData;

        // Represent the MyFunctionsBle.sleep() function
        jclass userObjectClass = mabai->env->GetObjectClass(mabai->myBleCallback);
        jmethodID methodId = mabai->env->GetMethodID(userObjectClass, "sleep", "(J)J");
        jobject myObject = mabai->myBleCallback;

        // Return SUCCESS if no method exists
        // No method means no implementation, means not required
        if (methodId == nullptr)
            return DC_STATUS_SUCCESS;

        jlong status = mabai->env->CallLongMethod(myObject, methodId, milliseconds);

        if (status == 1) {
            return DC_STATUS_SUCCESS;
        } else {
            return DC_STATUS_IO;
        }
    }
}

// libdivecomputer sends command under the form of a payload to be written to the DC
// *userData is passed by reference and is of type mabai_t
// *data is passed by reference and is of type byte[]??
// size is passed by value and is of type int
// *actual is passed by reference and is of type long
extern "C" {
    static dc_status_t
    mabai_write(void *userData, const void *data, size_t size, size_t *actual) {
        ALOG("mabai_write. Size: %d, Actual: %u\n", size, *actual);

        auto *mabai = (mabai_t *) userData;

        jclass userObjectClass = mabai->env->GetObjectClass(mabai->myBleCallback);
        jmethodID methodId = mabai->env->GetMethodID(userObjectClass, "write", "([BIJ)J");
        jobject myObject = mabai->myBleCallback;

        // Return SUCCESS if no method exists
        // No method means no implementation, means not required
        if (methodId == nullptr)
            return DC_STATUS_SUCCESS;

        // Create a java byte array.
        jbyteArray array = mabai->env->NewByteArray(size);
        mabai->env->SetByteArrayRegion(array, 0, size, static_cast<const jbyte *>(data));

        // TODO: Why do we need to increment nWrite here? Or do we need it at all?
        mabai->nWrite += size;

        /* Set the actual size. Normally the packet will be send all at once, so
        you probably won't have to deal with partial packets.*/
        *actual = size;

        // TODO: Can we pass size and actual as is here?
        ALOG("mabai_write-Calling MyFunctionsBle.write. Size: %d, Actual: %u\n", size, *actual);
        jlong status = mabai->env->CallLongMethod(myObject, methodId, array, size, actual);

        if (status == 1) {
            return DC_STATUS_SUCCESS;
        } else {
            return DC_STATUS_IO;
        }
    }
}

// *****
// ***** MABAI - JNI - libdivecomputer functions *****
// *****

extern "C"
JNIEXPORT jobject JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_customOpen(JNIEnv* env, jclass instance, jobject myFunctionsBle) {
    ALOG("customOpen.\n");

    mabai_t *mabai;

    static const dc_custom_cbs_t mabai_cbs = {
            mabai_set_timeout, /* set_timeout */
            mabai_set_break, /* set_break */
            mabai_set_dtr, /* set_dtr */
            mabai_set_rts, /* set_rts */
            mabai_get_lines, /* get_lines */
            mabai_get_available, /* get_available */
            mabai_configure, /* configure */
            mabai_poll, /* poll */
            mabai_read, /* read */
            mabai_write, /* write */
            mabai_ioctl, /* ioctl */
            mabai_flush, /* flush */
            mabai_purge, /* purge */
            mabai_sleep, /* sleep */
            mabai_close, /* close */
    };

    // Allocate memory for the mabai structure.
    mabai = (mabai_t*) malloc (sizeof(mabai_t));

    // No need to create a new instance of MyFunctionsBle
    // Otherwise it will point to a new instance with no services discovered!
    // Copy as is of the instance variable passed as an input argument
    mabai->myBleCallback = env->NewGlobalRef(myFunctionsBle);
    mabai->env = env;

//    // Create global variable for the cancel_cb()
//    // TODO: Test cancel and global variable
//    mCancel = env->NewGlobalRef(reinterpret_cast<jobject>(cancel));

    dc_iostream_t *iostream;
    dc_status_t status = DC_STATUS_SUCCESS;
    dc_context_t *context = nullptr;
    status = dc_context_new( &context);

    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        ALOG("dc_context_new() failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }

    // &iostream is passed by reference
    // context is passed by reference
    // transport is passed by value and only for Bluetooth Low Energy
    // callbacks or mabai_cbs is passed by reference. See vtable above
    // *userdata or mabai (structure) is passed by reference

    ALOG("Calling customOpen");
//    status = dc_custom_open (&iostream, context, DC_TRANSPORT_BLE, &mabai_cbs, mabai);
    status = dc_custom_open (&iostream, nullptr, DC_TRANSPORT_BLE, &mabai_cbs, mabai);

    if (status != DC_STATUS_SUCCESS) {
        ALOG("customOpen failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, failureL, notApplicableL, notApplicableL);
    } else {
        returnObject = getReturnDataObject(env, status, reinterpret_cast<jlong>(iostream), notApplicableL, notApplicableL);
    }

    cleanup:
    dc_context_free (context);

    return returnObject;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_deviceForeach(JNIEnv* env, jclass instance, jlong iostream, jlong device, jstring deviceName, jstring lastDiveFingerprint) {
    ALOG("deviceForeach.\n");

    dc_status_t status = DC_STATUS_SUCCESS;
    dc_context_t *context = nullptr;
    status = dc_context_new( &context);

    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        ALOG("dc_context_new() failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }

    auto *dc_device = reinterpret_cast<dc_device_t *>(device);
    auto dc_dive_callback = (dc_dive_callback_t) dive_cb;
    dctool_output_t *output = nullptr;

    // Build the eventdata
    event_data_t eventdata = {0};
    eventdata.cachedir = nullptr;

    // Register the event handler.
    int events = DC_EVENT_WAITING | DC_EVENT_PROGRESS | DC_EVENT_DEVINFO | DC_EVENT_CLOCK | DC_EVENT_VENDOR;
    ALOG("Calling dc_device_set_events.\n");
    status = dc_device_set_events (dc_device, events, event_cb, &eventdata);
    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        // TODO: More  clean up
        ALOG("dc_device_set_events failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }

    // Register the cancellation handler.
    ALOG("Calling dc_device_set_cancel.\n");
    status = dc_device_set_cancel (dc_device, cancel_cb, nullptr);
    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        // TODO: More  clean up
        ALOG("dc_device_set_cancel failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }

    // Build the fingerprint
    const char *fphex = reinterpret_cast<const char *>(lastDiveFingerprint);
    dc_buffer_t *fingerprint = nullptr;
    fingerprint = dctool_convert_hex2bin (fphex);

    // Register the fingerprint data.
//    if (fingerprint) {
        ALOG("Calling dc_device_set_fingerprint.\n");
        status = dc_device_set_fingerprint (dc_device, dc_buffer_get_data (fingerprint), dc_buffer_get_size (fingerprint));
        if (status != DC_STATUS_SUCCESS) {
            dc_context_free (context);
            // TODO: More  clean up
            ALOG("dc_device_set_fingerprint failed with status %s.", getStatus1(status));
            returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
            return returnObject;
        }
//    }

    ALOG("Calling dc_device_foreach.\n");

    status = dc_device_foreach(dc_device,dc_dive_callback, nullptr);
    jthrowable e = (*env).ExceptionOccurred();
    if (e != NULL) {
        env->ExceptionClear(); // clears the exception; e seems to remain valid

        jclass clazz = env->GetObjectClass(e);
        jmethodID getMessage = env->GetMethodID(clazz,
                                                "getMessage",
                                                "()Ljava/lang/String;");
        jstring message = (jstring)env->CallObjectMethod(e, getMessage);
        const char *mstr = env->GetStringUTFChars(message, NULL);
        // do whatever with mstr
        env->ReleaseStringUTFChars(message, mstr);
        env->DeleteLocalRef(message);
        env->DeleteLocalRef(clazz);
        env->DeleteLocalRef(e);
    }

    if (status != DC_STATUS_SUCCESS) {
        ALOG("dc_device_foreach failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, failureL);
    } else {
        // Return success
        // Dive data is actually returned in dve_cb
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, successL);
    }

    cleanup:
    dc_context_free (context);
    // TODO: More  clean up

    return returnObject;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_deviceOpen(JNIEnv* env, jclass instance, jstring vendor, jstring product, jstring devname, jlong iostream) {
    ALOG("deviceOpen.\n");

    dc_status_t status = DC_STATUS_SUCCESS;
    dc_context_t *context = nullptr;
    status = dc_context_new( &context);

    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        ALOG("dc_context_new() failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }

    // Create a descriptor iterator.
    dc_iterator_t *iterator = nullptr;
    dc_descriptor_iterator (&iterator);

    // Copy strings to native format in order to compare them
    const char *nativeVendor = env->GetStringUTFChars(vendor,nullptr);
    const char *nativeProduct = env->GetStringUTFChars(product,nullptr);
    const char *nativeDevname = env->GetStringUTFChars(devname,nullptr);

    dc_bluetooth_address_t address = 0;

    // NOTE: The saved computer should already have all the valid information
    //       Even if the user didn't know his real; computer model, MABAI should update
    //       with the latest valid libdivecomputer info

    // Loop over the descriptors to find the desired dive computer
    dc_descriptor_t *descriptor = nullptr;
    int resCmp = 0;
    int resCmp2 = 0;

    while (dc_iterator_next (iterator, &descriptor) == DC_STATUS_SUCCESS) {
        // Compare by vendor and product family
        resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendor, strlen(nativeVendor));
        resCmp2 = strncmp(dc_descriptor_get_product(descriptor), nativeProduct, strlen(nativeProduct));
        if (resCmp == 0 && resCmp2 == 0) {
            // Getting out of the loop
            // Therefore not freeing the descriptor
            break;
        }
        dc_descriptor_free (descriptor);
    }

    // Free the iterator.
    dc_iterator_free (iterator);

    // Release the handles
    env->ReleaseStringUTFChars(vendor,nativeVendor);
    env->ReleaseStringUTFChars(product,nativeProduct);
    env->ReleaseStringUTFChars(devname,nativeDevname);

    dc_device_t *deviceOut;
    auto *dc_iostream = reinterpret_cast<dc_iostream_t *>(iostream);
    ALOG("Calling dc_device.\n");
//    status = dc_device_open(&deviceOut, context, descriptor, dc_iostream);
    status = dc_device_open(&deviceOut, nullptr, descriptor, dc_iostream);

    if (status != DC_STATUS_SUCCESS) {
        ALOG("dc_device_open failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, failureL, notApplicableL);
    } else {
        returnObject = getReturnDataObject(env, status, notApplicableL, reinterpret_cast<jlong>(deviceOut), notApplicableL);
    }

    cleanup:
    dc_descriptor_free (descriptor);
    dc_context_free (context);

    return returnObject;
}

#define TEST1 1
#define TEST2 1
#define TEST3 1
#define TEST4 1
#define TEST5 1

extern "C"
JNIEXPORT jobject JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_download(JNIEnv* env, jclass instance, jobject myFunctionsBle, jstring vendor, jstring product, jstring deviceName, jstring lastDiveFingerprint) {
    ALOG("downLoad.\n");

    // ***** dc_custom_open *****
    // To get the iostream

    mabai_t *mabai;

    static const dc_custom_cbs_t mabai_cbs = {
#ifdef TEST1
            mabai_set_timeout, /* set_timeout */
            mabai_set_break, /* set_break */
            mabai_set_dtr, /* set_dtr */
            mabai_set_rts, /* set_rts */
            mabai_get_lines, /* get_lines */
            mabai_get_available, /* get_available */
            mabai_configure, /* configure */
            mabai_poll, /* poll */
            mabai_read, /* read */
            mabai_write, /* write */
            mabai_ioctl, /* ioctl */
            mabai_flush, /* flush */
            mabai_purge, /* purge */
            mabai_sleep, /* sleep */
            mabai_close, /* close */
#else
            NULL, /* set_timeout */
            NULL, /* set_break */
            NULL, /* set_dtr */
            NULL, /* set_rts */
            NULL, /* get_lines */
            NULL, /* get_available */
            NULL, /* configure */
            NULL, /* poll */
            NULL, /* read */
            NULL, /* write */
            NULL, /* ioctl */
            NULL, /* flush */
            NULL, /* purge */
            NULL, /* sleep */
            NULL, /* close */
#endif
    };

    // Allocate memory for the mabai structure.
    mabai = (mabai_t*) malloc (sizeof(mabai_t));

    // No need to create a new instance of MyFunctionsBle
    // Otherwise it will point to a new instance with no services discovered!
    // Copy as is of the instance variable passed as an input argument
    mabai->myBleCallback = env->NewGlobalRef(myFunctionsBle);
    mabai->env = env;

#if 0
    // Create global variable for the cancel_cb()
    // TODO: Test cancel and global variable
    mCancel = env->NewGlobalRef(reinterpret_cast<jobject>(cancel));
#endif

    dc_iostream_t *iostream;
    dc_status_t status = DC_STATUS_SUCCESS;
    dc_context_t *context = nullptr;
    status = dc_context_new( &context);

    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        ALOG("dc_context_new() failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }

    // &iostream is passed by reference
    // context is passed by reference
    // transport is passed by value and only for Bluetooth Low Energy
    // callbacks or mabai_cbs is passed by reference. See vtable above
    // *userdata or mabai (structure) is passed by reference

    ALOG("Calling customOpen");
//    status = dc_custom_open (&iostream, context, DC_TRANSPORT_BLE, &mabai_cbs, mabai);
    status = dc_custom_open (&iostream, nullptr, DC_TRANSPORT_BLE, &mabai_cbs, mabai);

    if (status != DC_STATUS_SUCCESS) {
        ALOG("customOpen failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, failureL, notApplicableL, notApplicableL);
    }

    // ***** dc_device_open *****
    // To get the device

    // Create a descriptor iterator.
    dc_iterator_t *iterator = nullptr;
    dc_descriptor_iterator (&iterator);

    // Copy strings to native format in order to compare them
    const char *nativeVendor = env->GetStringUTFChars(vendor,nullptr);
    const char *nativeProduct = env->GetStringUTFChars(product,nullptr);
    const char *nativeDeviceName = env->GetStringUTFChars(deviceName,nullptr);

    dc_bluetooth_address_t address = 0;

    // NOTE: The saved computer should already have all the valid information
    //       Even if the user didn't know his real; computer model, MABAI should update
    //       with the latest valid libdivecomputer info

    // Loop over the descriptors to find the desired dive computer
    dc_descriptor_t *descriptor = nullptr;
    int resCmp = 0;
    int resCmp2 = 0;

    while (dc_iterator_next (iterator, &descriptor) == DC_STATUS_SUCCESS) {
        // Compare by vendor and product family
        resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendor, strlen(nativeVendor));
        resCmp2 = strncmp(dc_descriptor_get_product(descriptor), nativeProduct, strlen(nativeProduct));
        if (resCmp == 0 && resCmp2 == 0) {
            // Getting out of the loop
            // Therefore not freeing the descriptor
            break;
        }
        dc_descriptor_free (descriptor);
    }

    // Free the iterator.
    dc_iterator_free (iterator);

    // Release the handles
    env->ReleaseStringUTFChars(vendor,nativeVendor);
    env->ReleaseStringUTFChars(product,nativeProduct);
    env->ReleaseStringUTFChars(deviceName,nativeDeviceName);

    dc_device_t *deviceOut;
    auto *dc_iostream = reinterpret_cast<dc_iostream_t *>(iostream);
    ALOG("Calling dc_device.\n");
//    status = dc_device_open(&deviceOut, context, descriptor, dc_iostream);
    status = dc_device_open(&deviceOut, nullptr, descriptor, dc_iostream);

    if (status != DC_STATUS_SUCCESS) {
        ALOG("dc_device_open failed. Status is %s", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, failureL, notApplicableL);
    }

    // Cleanup
    dc_descriptor_free (descriptor);

    // ***** deviceForeach *****

//    auto *dc_device = reinterpret_cast<dc_device_t *>(device);
    auto dc_dive_callback = (dc_dive_callback_t) dive_cb;
    dctool_output_t *output = nullptr;

    // Build the eventdata
    event_data_t eventdata = {0};
    eventdata.cachedir = nullptr;

#ifdef TEST2
    // Register the event handler.
    int events = DC_EVENT_WAITING | DC_EVENT_PROGRESS | DC_EVENT_DEVINFO | DC_EVENT_CLOCK | DC_EVENT_VENDOR;
    ALOG("Calling dc_device_set_events.\n");
    status = dc_device_set_events (deviceOut, events, event_cb, &eventdata);
    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        // TODO: More  clean up
        ALOG("dc_device_set_events failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }
#endif

#ifdef TEST3
    // Register the cancellation handler.
    ALOG("Calling dc_device_set_cancel.\n");
    status = dc_device_set_cancel (deviceOut, cancel_cb, nullptr);
    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        // TODO: More  clean up
        ALOG("dc_device_set_cancel failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }
#endif

#ifdef TEST4
    // Build the fingerprint
    const char *fphex = reinterpret_cast<const char *>(lastDiveFingerprint);
    dc_buffer_t *fingerprint = nullptr;
    fingerprint = dctool_convert_hex2bin (fphex);

    // Register the fingerprint data.
    ALOG("Calling dc_device_set_fingerprint.\n");
    status = dc_device_set_fingerprint (deviceOut, dc_buffer_get_data (fingerprint), dc_buffer_get_size (fingerprint));
    if (status != DC_STATUS_SUCCESS) {
        dc_context_free (context);
        // TODO: More  clean up
        ALOG("dc_device_set_fingerprint failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);
        return returnObject;
    }
#endif

    ALOG("Calling dc_device_foreach.\n");

#ifdef TEST5
    status = dc_device_foreach(deviceOut,dc_dive_callback, nullptr);
#else
    status = dc_device_foreach(deviceOut,nullptr, nullptr);
#endif
    jthrowable e = (*env).ExceptionOccurred();
    if (e != NULL) {
        env->ExceptionClear(); // clears the exception; e seems to remain valid

        jclass clazz = env->GetObjectClass(e);
        jmethodID getMessage = env->GetMethodID(clazz,
                                                "getMessage",
                                                "()Ljava/lang/String;");
        jstring message = (jstring)env->CallObjectMethod(e, getMessage);
        const char *mstr = env->GetStringUTFChars(message, NULL);
        // do whatever with mstr??
        env->ReleaseStringUTFChars(message, mstr);
        env->DeleteLocalRef(message);
        env->DeleteLocalRef(clazz);
        env->DeleteLocalRef(e);
    }

    if (status != DC_STATUS_SUCCESS) {
        ALOG("dc_device_foreach failed with status %s.", getStatus1(status));
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, failureL);
    }

    // Dive data is actually returned in MyFunctionsBle.dve_cb

    // ***** dc_iostream_close *****

    ALOG("Calling dc_iostream_close");
    status = dc_iostream_close(reinterpret_cast<dc_iostream_t *>(iostream));

    if (status != DC_STATUS_SUCCESS) {
        ALOG("dc_iostream_close failed. Status is %s", getStatus1(status));
    } else {
        // Return success
        returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, successL);
    }

    // Cleanup
    dc_context_free (context);
    // TODO: More  clean up

    // Return success
    return returnObject;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_getArrayString(JNIEnv* env, jclass instance) {
    ALOG("getArrayString.\n");

    jobjectArray ret;
    int i;

    char *data[5] = {const_cast<char *>("Shearwater")
                     , const_cast<char *>("Scubapro")
                     , const_cast<char *>("Mares")
                     , const_cast<char *>("Garmin")
                     , const_cast<char *>("Sunto")
    };

    ret = (jobjectArray)env->NewObjectArray(5,env->FindClass("java/lang/String"),env->NewStringUTF(""));

    for(i=0;i<5;i++) env->SetObjectArrayElement(ret,i,env->NewStringUTF(data[i]));

    return(ret);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_getSingleString(JNIEnv* env, jclass instance) {
    ALOG("getSingleString.\n");

    jstring test = env->NewStringUTF("Test");
    return test;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_getSupportedDiveComputers(JNIEnv* env, jclass instance, jint transportType) {
    ALOG("getSupportedDiveComputers.\n");

    // Create an iterator
    dc_iterator_t *iterator = nullptr;
    dc_descriptor_iterator (&iterator);

    // Loop over the descriptors to count the number of supported dive computers
    // No need to keep a descriptor alive
    jsize noSupportedDiveComputer = 0;
    unsigned int transports = 0;
    dc_descriptor_t *descriptor = nullptr;

    while (dc_iterator_next (iterator, &descriptor) == DC_STATUS_SUCCESS) {
        transports = dc_descriptor_get_transports(descriptor);

        // Only count the Bluetooth Classic or BLE  dive computers
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                noSupportedDiveComputer++;
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            noSupportedDiveComputer++;
        }

        dc_descriptor_free (descriptor);
    }

    // Free the iterator.
    dc_iterator_free (iterator);

    // Start loading the return array

    jclass computerCls = env->FindClass("ca/myairbuddyandi/LibDiveComputer");
    jclass intCls = env->FindClass("java/lang/Integer");

    jmethodID computerInit = env->GetMethodID(computerCls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)V");
    jmethodID intInit = env->GetMethodID(intCls, "<init>", "(I)V");

    auto supportedComputers = (jobjectArray)env->NewObjectArray(noSupportedDiveComputer, computerCls, nullptr);

    jstring vendor;    // String
    jstring product;   // String
    jobject type;      // Integer
    jobject model;     // Integer
    jobject transport; // Integer
    jobject computer;  // POJO

    if (nullptr == computerInit) return nullptr;
    if (nullptr == intInit) return nullptr;

    // Create an iterator.
    dc_iterator_t *iterator2 = nullptr;
    dc_descriptor_iterator (&iterator2);

    // Loop over the descriptors to load the supported dive computers
    // No need to keep a descriptor2 alive
    noSupportedDiveComputer = 0;
    dc_descriptor_t *descriptor2 = nullptr;

    while (dc_iterator_next (iterator2, &descriptor2) == DC_STATUS_SUCCESS) {
        vendor = env->NewStringUTF(dc_descriptor_get_vendor(descriptor2));
        product = env->NewStringUTF(dc_descriptor_get_product(descriptor2));
        type = env->NewObject(intCls, intInit, (jint) dc_descriptor_get_type (descriptor2));
        model = env->NewObject(intCls, intInit, (jint) dc_descriptor_get_model (descriptor2));
        transport = env->NewObject(intCls, intInit, (jint) dc_descriptor_get_transports(descriptor2));
        computer = env->NewObject(computerCls, computerInit, vendor, product, type, model, transport);

        transports = dc_descriptor_get_transports(descriptor2);

        // Only add/count the Bluetooth Classic or BLE  dive computers
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                env->SetObjectArrayElement(supportedComputers, noSupportedDiveComputer, computer);
                noSupportedDiveComputer++;
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = BLuetooth Low Energy
            env->SetObjectArrayElement(supportedComputers, noSupportedDiveComputer, computer);
            noSupportedDiveComputer++;
        }

        env->DeleteLocalRef(vendor);
        env->DeleteLocalRef(product);
        env->DeleteLocalRef(type);
        env->DeleteLocalRef(model);
        env->DeleteLocalRef(transport);
        env->DeleteLocalRef(computer);

        dc_descriptor_free (descriptor2);
    }

    // Free the iterator.
    dc_iterator_free (iterator2);

    return (supportedComputers);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_getSupportedDiveComputerPerVendor(JNIEnv* env, jclass instance, jint transportType, jstring vendorFilter) {
    ALOG("getSupportedDiveComputerPerVendor.\n");

    // Create an iterator.
    dc_iterator_t *iterator = nullptr;
    dc_descriptor_iterator (&iterator);

    // Copy strings to native format in order to compare it
    const char *nativeVendorFilter = env->GetStringUTFChars(vendorFilter,nullptr);

    // Loop over the descriptors to count the number of supported dive computers
    // No need to keep a descriptor alive
    jsize noSupportedDiveComputer = 0;
    unsigned int transports = 0;
    dc_descriptor_t *descriptor = nullptr;
    int resCmp;

    while (dc_iterator_next (iterator, &descriptor) == DC_STATUS_SUCCESS) {
        transports = dc_descriptor_get_transports(descriptor);

        // Only count the Bluetooth Classic or BLE  dive computers
        // For a given vendor
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendorFilter, strlen(nativeVendorFilter));
                if (resCmp == 0) {
                    noSupportedDiveComputer++;
                }
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendorFilter, strlen(nativeVendorFilter));
            if (resCmp == 0) {
                noSupportedDiveComputer++;
            }
        }

        dc_descriptor_free (descriptor);
    }

    // Free the iterator.
    dc_iterator_free (iterator);

    // Start loading the return array

    jclass computerCls = env->FindClass("ca/myairbuddyandi/LibDiveComputer");
    jclass intCls = env->FindClass("java/lang/Integer");

    jmethodID computerInit = env->GetMethodID(computerCls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)V");
    jmethodID intInit = env->GetMethodID(intCls, "<init>", "(I)V");

    auto supportedComputers = (jobjectArray)env->NewObjectArray(noSupportedDiveComputer, computerCls, nullptr);

    jstring vendor;    // String
    jstring product;   // String
    jobject type;      // Integer
    jobject model;     // Integer
    jobject transport; // Integer
    jobject computer;  // POJO

    if (nullptr == computerInit) return nullptr;
    if (nullptr == intInit) return nullptr;

    // Create an iterator.
    dc_iterator_t *iterator2 = nullptr;
    dc_descriptor_iterator (&iterator2);

    // Loop over the descriptors to load the supported dive computers
    // No need to keep a descriptor2 alive
    noSupportedDiveComputer = 0;
    dc_descriptor_t *descriptor2 = nullptr;

    while (dc_iterator_next (iterator2, &descriptor2) == DC_STATUS_SUCCESS) {
        vendor = env->NewStringUTF(dc_descriptor_get_vendor(descriptor2));
        product = env->NewStringUTF(dc_descriptor_get_product(descriptor2));
        type = env->NewObject(intCls, intInit, (jint) dc_descriptor_get_type (descriptor2));
        model = env->NewObject(intCls, intInit, (jint) dc_descriptor_get_model (descriptor2));
        transport = env->NewObject(intCls, intInit, (jint) dc_descriptor_get_transports(descriptor2));
        computer = env->NewObject(computerCls, computerInit, vendor, product, type, model, transport);

        transports = dc_descriptor_get_transports(descriptor2);

        // Only count the Bluetooth Classic or BLE  dive computers
        // For a given vendor
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendorFilter, strlen(nativeVendorFilter));
                if (resCmp == 0) {
                    env->SetObjectArrayElement(supportedComputers, noSupportedDiveComputer, computer);
                    noSupportedDiveComputer++;
                }
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendorFilter, strlen(nativeVendorFilter));
            if (resCmp == 0) {
                env->SetObjectArrayElement(supportedComputers, noSupportedDiveComputer, computer);
                noSupportedDiveComputer++;
            }
        }

        env->DeleteLocalRef(vendor);
        env->DeleteLocalRef(product);
        env->DeleteLocalRef(type);
        env->DeleteLocalRef(model);
        env->DeleteLocalRef(transport);
        env->DeleteLocalRef(computer);

        dc_descriptor_free (descriptor2);
    }

    // Free the iterator.
    dc_iterator_free (iterator2);

    return (supportedComputers);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_getSupportedProductsPerVendor(JNIEnv* env, jclass instance, jint transportType, jstring vendorFilter) {
    ALOG("getSupportedProductsPerVendor.\n");

    // Create an iterator
    dc_iterator_t *iterator = nullptr;
    dc_descriptor_iterator (&iterator);

    // Copy strings to native format in order to compare it
    const char *nativeVendorFilter = env->GetStringUTFChars(vendorFilter,nullptr);

    // Loop over the descriptors to count the number of products
    // No need to keep a descriptor alive
    jsize noProducts = 0;
    unsigned int transports = 0;
    dc_descriptor_t *descriptor = nullptr;
    int resCmp;

    while (dc_iterator_next (iterator, &descriptor) == DC_STATUS_SUCCESS) {
        transports = dc_descriptor_get_transports(descriptor);

        // Only count the Bluetooth Classic or BLE  dive computers
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendorFilter, strlen(nativeVendorFilter));
                if (resCmp == 0) {
                    noProducts++;
                }
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativeVendorFilter, strlen(nativeVendorFilter));
            if (resCmp == 0) {
                noProducts++;
            }
        }

        dc_descriptor_free (descriptor);
    }

    // Free the iterator.
    dc_iterator_free (iterator);

    // Start loading the return array

    auto products = (jobjectArray)env->NewObjectArray(noProducts,env->FindClass("java/lang/String"),env->NewStringUTF(""));
    const char *product;

    // Create an iterator.
    dc_iterator_t *iterator2 = nullptr;
    dc_descriptor_iterator (&iterator2);

    // Loop over the descriptors to load the supported products
    // No need to keep a descriptor2 alive
    noProducts = 0;
    dc_descriptor_t *descriptor2 = nullptr;

    while (dc_iterator_next (iterator2, &descriptor2) == DC_STATUS_SUCCESS) {
        product = dc_descriptor_get_product(descriptor2);
        transports = dc_descriptor_get_transports(descriptor2);

        // Only add/count the Bluetooth Classic or BLE  dive computers
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                resCmp = strncmp(dc_descriptor_get_vendor(descriptor2), nativeVendorFilter, strlen(nativeVendorFilter));
                if (resCmp == 0) {
                    env->SetObjectArrayElement(products, noProducts, env->NewStringUTF(product));
                    noProducts++;
                }
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            resCmp = strncmp(dc_descriptor_get_vendor(descriptor2), nativeVendorFilter, strlen(nativeVendorFilter));
            if (resCmp == 0) {
                env->SetObjectArrayElement(products, noProducts, env->NewStringUTF(product));
                noProducts++;
            }
        }

        dc_descriptor_free (descriptor2);
    }

    // Free the iterator.
    dc_iterator_free (iterator2);

    return (products);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_getSupportedVendors(JNIEnv* env, jclass instance, jint transportType) {
    ALOG("getSupportedVendors.\n");

    // Create an iterator
    dc_iterator_t *iterator = nullptr;
    dc_descriptor_iterator (&iterator);

    // Copy strings to native format in order to compare it
    jstring previousVendor = env->NewStringUTF("Previous");
    const char *nativePreviousVendor = env->GetStringUTFChars(previousVendor,nullptr);

    // Loop over the descriptors to count the number of vendors
    // No need to keep a descriptor alive
    jsize noVendors = 0;
    unsigned int transports = 0;
    dc_descriptor_t *descriptor = nullptr;
    int resCmp;

    while (dc_iterator_next (iterator, &descriptor) == DC_STATUS_SUCCESS) {
        transports = dc_descriptor_get_transports(descriptor);

        // Only count the Bluetooth Classic or BLE  dive computers
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativePreviousVendor, strlen(nativePreviousVendor));
                if (resCmp != 0) {
                    noVendors++;
                    nativePreviousVendor = dc_descriptor_get_vendor(descriptor);
                }
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            resCmp = strncmp(dc_descriptor_get_vendor(descriptor), nativePreviousVendor, strlen(nativePreviousVendor));
            if (resCmp != 0) {
                noVendors++;
                nativePreviousVendor = dc_descriptor_get_vendor(descriptor);
            }
        }

        dc_descriptor_free (descriptor);
    }

    // Free the iterator.
    dc_iterator_free (iterator);

    // Start loading the return array

    auto vendors = (jobjectArray)env->NewObjectArray(noVendors,env->FindClass("java/lang/String"),env->NewStringUTF(""));
    const char *vendor;

    // Create an iterator.
    dc_iterator_t *iterator2 = nullptr;
    dc_descriptor_iterator (&iterator2);

    // Loop over the descriptors to load the supported vendors
    // No need to keep a descriptor2 alive
    noVendors = 0;
    dc_descriptor_t *descriptor2 = nullptr;
    const char *nativePreviousVendor2 = env->GetStringUTFChars(previousVendor,nullptr);

    while (dc_iterator_next (iterator2, &descriptor2) == DC_STATUS_SUCCESS) {
        vendor = dc_descriptor_get_vendor(descriptor2);
        transports = dc_descriptor_get_transports(descriptor2);

        // Only add/count the Bluetooth Classic or BLE  dive computers
        if (transportType == (DC_TRANSPORT_SERIAL | DC_TRANSPORT_BLUETOOTH | DC_TRANSPORT_BLE)) {
            // Dual transport. Bluetooth Classic OR Bluetooth Low Energy
            if ((transports & DC_TRANSPORT_BLE) || (transports & DC_TRANSPORT_BLUETOOTH)) {
                resCmp = strncmp(dc_descriptor_get_vendor(descriptor2), nativePreviousVendor2, strlen(nativePreviousVendor2));
                if (resCmp != 0) {
                    env->SetObjectArrayElement(vendors, noVendors, env->NewStringUTF(vendor));
                    noVendors++;
                    nativePreviousVendor2 = dc_descriptor_get_vendor(descriptor2);
                }
            }
        } else if (transports == transportType) {
            // 16 = Bluetooth Classic
            // 32 = Bluetooth Low Energy
            resCmp = strncmp(dc_descriptor_get_vendor(descriptor2), nativePreviousVendor2, strlen(nativePreviousVendor2));
            if (resCmp != 0) {
                env->SetObjectArrayElement(vendors, noVendors, env->NewStringUTF(vendor));
                noVendors++;
                nativePreviousVendor2 = dc_descriptor_get_vendor(descriptor2);
            }
        }

        dc_descriptor_free (descriptor2);
    }

    // Free the iterator.
    dc_iterator_free (iterator2);

    return (vendors);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_iostreamClose(JNIEnv* env, jclass instance, jlong iostream) {
    ALOG("iostreamClose.\n");

    dc_status_t status;

    ALOG("Calling dc_iostream_close");
    status = dc_iostream_close(reinterpret_cast<dc_iostream_t *>(iostream));

    if (status != DC_STATUS_SUCCESS) {
        ALOG("dc_iostream_close failed. Status is %s", getStatus1(status));
    }

    returnObject = getReturnDataObject(env, status, notApplicableL, notApplicableL, notApplicableL);

    return returnObject;
}

// TODO: To be tested
extern "C"
JNIEXPORT void JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_setCancel(JNIEnv* env, jclass instance, jint cancel) {
    ALOG("setCancel.\n");

//    mCancel = reinterpret_cast<jobject>(cancel);
    mCancel = cancel;
}

// TODO: To be removed
extern "C"
JNIEXPORT void JNICALL
Java_ca_myairbuddyandi_MyFunctionsLibDiveComputer_testGetMethod(JNIEnv* env, jclass instance, jobject myFunctionsBle, jint timeout) {
    ALOG("testGetMethod.\n");

    jclass userDataClass = env->FindClass("ca/myairbuddyandi/MyFunctionsBle");
    jmethodID methodId = env->GetMethodID(userDataClass, "close", "()J");
    jobject object = env->AllocObject(userDataClass);
    env->CallLongMethod(object, methodId);

    jclass userObjectClass = env->GetObjectClass(myFunctionsBle);
    jmethodID methodId2 = env->GetMethodID(userObjectClass, "close", "()J");
    jobject object2 = env->AllocObject(userObjectClass);
    env->CallLongMethod(object2, methodId2);

    jclass userDataClass3 = env->FindClass("ca/myairbuddyandi/MyFunctionsBle");
    jmethodID methodId3 = env->GetMethodID(userDataClass3, "close", "()J");
    jobject object3 = myFunctionsBle;
    env->CallLongMethod(object3, methodId3);

    jclass userObjectClass2 = env->GetObjectClass(myFunctionsBle);
    jmethodID methodId4 = env->GetMethodID(userObjectClass2, "close", "()J");
    jobject object4 = myFunctionsBle;
    env->CallLongMethod(object4, methodId3);

    jclass userDataClass5 = env->FindClass("ca/myairbuddyandi/MyFunctionsBle");
    jmethodID methodId5 = env->GetMethodID(userDataClass5, "close", "()J");
    env->CallLongMethod(myFunctionsBle, methodId5);

    jclass userObjectClass3 = env->GetObjectClass(myFunctionsBle);
    jmethodID methodId6 = env->GetMethodID(userObjectClass3, "close", "()J");
    env->CallLongMethod(myFunctionsBle, methodId6);
}