package ca.myairbuddyandi;

/**
 * Created by Michel on 2023-07-04.
 * Holds all of the value coming back from myairbuddyandi.cpp and libdivecomputer
 *
 */

// NOTE No need to make them private and create setters and getters
//      Much easier that way in myairbuddyandi.cpp
public class LibDiveComputerReturnData {
    public String status;
    public long iostream;
    public long device;
    public long diveData;
}
