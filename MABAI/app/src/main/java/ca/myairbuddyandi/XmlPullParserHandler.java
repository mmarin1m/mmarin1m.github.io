package ca.myairbuddyandi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class XmlPullParserHandler {

    // Static

    // Public

    // Protected

    // Private
    private final List<Cylinder> cylinders;
    {
        cylinders = new ArrayList<>();
    }
    private final List<CylinderType> cylinderTypes;
    {
        cylinderTypes = new ArrayList<>();
    }
    private final List<DbVersion> dbVersions;
    {
        dbVersions = new ArrayList<>();
    }
    private final List<Diver> divers;
    {
        divers = new ArrayList<>();
    }
    private final List<DivePlan> divePlans;
    {
        divePlans = new ArrayList<>();
    }
    private final List<DiverDive> diverDives;
    {
        diverDives = new ArrayList<>();
    }
    private final List<DiverDiveGroup> diverDiveGroups;
    {
        diverDiveGroups = new ArrayList<>();
    }
    private final List<DiverDiveGroupCyl> diverDiveGroupCyls;
    {
        diverDiveGroupCyls = new ArrayList<>();
    }
    private final List<Dive> dives;
    {
        dives = new ArrayList<>();
    }
    private final List<DiveSegment> diveSegments;
    {
        diveSegments = new ArrayList<>();
    }
    private final List<DiveType> diveTypes;
    {
        diveTypes = new ArrayList<>();
    }
    private final List<DynamicSpinner> dynamicSpinners;
    {
        dynamicSpinners = new ArrayList<>();
    }
    private final List<Groupp> groupps;
    {
        groupps = new ArrayList<>();
    }
    private final List<GrouppCylinder> grouppCylinders;
    {
        grouppCylinders = new ArrayList<>();
    }
    private final List<GrouppType> grouppTypes;
    {
        grouppTypes = new ArrayList<>();
    }
    private final List<SegmentType> segmentTypes;
    {
        segmentTypes = new ArrayList<>();
    }
    private final List<Setting> settings;
    {
        settings = new ArrayList<>();
    }
    private final List<State> states;
    {
        states = new ArrayList<>();
    }
    private final List<UsageType> usageTypes;
    {
        usageTypes = new ArrayList<>();
    }

    private Boolean mCurrentTableFound;
    private Boolean mEndProcess;

    private String mColumnValue;
    private String mTableName;
    private String mColumnName;
    private String mDatabaseName;

    private Cylinder mCylinder;
    private CylinderType mCylinderType;
    private DbVersion mDbVersion;
    private Dive mDive;
    private DivePlan mDivePlan;
    private Diver mDiver;
    private DiverDive mDiverDive;
    private DiverDiveGroup mDiverDiveGroup;
    private DiverDiveGroupCyl mDiverDiveGroupCyl;
    private DiveSegment mDiveSegment;
    private DiveType mDiveType;
    private DynamicSpinner mDynamicSpinner;
    private Groupp mGroupp;
    private GrouppCylinder mGrouppCylinder;
    private GrouppType mGrouppType;
    private SegmentType mSegmentType;
    private Setting mSetting;
    private State mState;
    private UsageType mUsageType;

    // End of variables

    // Public constructor
    public XmlPullParserHandler() {
    }

    // My functions

    public List<Cylinder> getCylinders() {
        return cylinders;
    }
    public List<CylinderType> getCylinderTypes() {
        return cylinderTypes;
    }
    // NOTE: Reserved for future use
    public List<DbVersion> getDbVersions() {
        return dbVersions;
    }
    public List<Dive> getDives() {
        return dives;
    }
    public List<DivePlan> getDivePlans() {
        return divePlans;
    }
    public List<Diver> getDivers() {
        return divers;
    }
    // NOTE: Reserved for future use
    public List<DiverDive> getDiverDives() {
        return diverDives;
    }
    public List<DiverDiveGroup> getDiverDiveGroups() {
        return diverDiveGroups;
    }
    public List<DiveSegment> getDiveSegments() {
        return diveSegments;
    }
    public List<DiveType> getDiveTypes() {
        return diveTypes;
    }
    public List<DynamicSpinner> getDynamicSpinners() {
        return dynamicSpinners;
    }
    public List<Groupp> getGroupps() {
        return groupps;
    }
    public List<GrouppCylinder> getGrouppCylinders() {
        return grouppCylinders;
    }
    public List<GrouppType> getGrouppTypes() {
        return grouppTypes;
    }
    public List<Setting> getSettings() {
        return settings;
    }
    public List<State> getStates() {
        return states;
    }
    public List<UsageType> getUsageTypes() {
        return usageTypes;
    }

    public List<Cylinder> parseCylinders(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("cylinder");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of Cylinder
                            mCylinder = new Cylinder();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("cylinder")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add CylinderType object to list
                                    if (mCurrentTableFound) {
                                        cylinders.add(mCylinder);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "cylinder_no":
                                                mCylinder.setCylinderNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "diver_no":
                                                mCylinder.setDiverNo(Long.valueOf(mColumnValue));
                                                break;
                                            case "cylinder_type":
                                                mCylinder.setCylinderTypeLoad(mColumnValue);
                                                break;
                                            case "volume":
                                                mCylinder.setVolume(Double.valueOf(mColumnValue));
                                                break;
                                            case "rated_pressure":
                                                mCylinder.setRatedPressure(Double.valueOf(mColumnValue));
                                                break;
                                            case "brand":
                                                mCylinder.setBrand(mColumnValue);
                                                break;
                                            case "model":
                                                mCylinder.setModel(mColumnValue);
                                                break;
                                            case "serial_no":
                                                mCylinder.setSerialNo(mColumnValue);
                                                break;
                                            case "last_vip":
                                                // Formats looks like 946702800000
                                                mCylinder.setLastVip(MyFunctions.convertDateFromLongToDate(Long.valueOf(mColumnValue)));
                                                break;
                                            case "last_hydro":
                                                // Formats looks like 946702800000
                                                mCylinder.setLastHydro(MyFunctions.convertDateFromLongToDate(Long.valueOf(mColumnValue)));
                                                break;
                                            case "color":
                                                mCylinder.setTankColor(mColumnValue);
                                                break;
                                            case "weight_full":
                                                mCylinder.setWeightFull(Double.valueOf(mColumnValue));
                                                break;
                                            case "weight_empty":
                                                mCylinder.setWeightEmpty(Double.valueOf(mColumnValue));
                                                break;
                                            case "buoyancy_full":
                                                mCylinder.setBuoyancyFull(Double.valueOf(mColumnValue));
                                                break;
                                            case "buoyancy_empty":
                                                mCylinder.setBuoyancyEmpty(Double.valueOf(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return cylinders;
    }

    public List<CylinderType> parseCylinderTypes(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("cylinder_type");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of CylinderType
                            mCylinderType = new CylinderType();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("cylinder_type")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add CylinderType object to list
                                    if (mCurrentTableFound) {
                                        cylinderTypes.add(mCylinderType);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "cylinder_type":
                                                mCylinderType.setCylinderType(mColumnValue);
                                                break;
                                            case "description":
                                                mCylinderType.setDescription(mColumnValue);
                                                break;
                                            case "volume":
                                                mCylinderType.setVolume(Double.valueOf(mColumnValue));
                                                break;
                                            case "rated_pressure":
                                                mCylinderType.setRatedPressure(Double.valueOf(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return cylinderTypes;
    }

    public String parseDatabaseName(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);
            int eventType = parser.getEventType();
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                // NOTE: Leave as if
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("database")) {
                            mDatabaseName = parser.getAttributeValue(null, "name");
                            mEndProcess = true;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return mDatabaseName;
    }

    public List<DbVersion> parseDbVersions(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("db_version");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DbVersion
                            mDbVersion = new DbVersion();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("db_version")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DbVersion object to list
                                    if (mCurrentTableFound) {
                                        dbVersions.add(mDbVersion);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        mDbVersion.setColumnName(mColumnName);
                                        mDbVersion.setColumnValue(mColumnValue);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return dbVersions;
    }

    public List<Dive> parseDives(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("dive");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of Dive
                            mDive = new Dive();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("dive")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add Dive object to list
                                    if (mCurrentTableFound) {
                                        dives.add(mDive);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "dive_no":
                                                mDive.setDiveNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_type":
                                                mDive.setDiveTypeLoad(mColumnValue);
                                                break;
                                            case "salinity":
                                                mDive.setSalinity(Integer.valueOf(mColumnValue).equals(MyConstants.ONE_INT));
                                                break;
                                            case "date":
                                                // Format looks like 304444800000
                                                mDive.setDate(MyFunctions.convertDateFromLongToDate(Long.valueOf(mColumnValue)));
                                                // Format looks like 16:09
                                                mDive.setTimeIn(MyFunctions.getHourString(mDive.getDate())
                                                                + ":"
                                                                + MyFunctions.getMinuteString(mDive.getDate())
                                                               );
                                                mDive.setHour(MyFunctions.getHour(mDive.getTimeIn()));
                                                mDive.setMinute(MyFunctions.getMinute(mDive.getTimeIn()));
                                                break;
                                            case "bottom_time":
                                                mDive.setBottomTime(Double.valueOf(mColumnValue));
                                                break;
                                            case "average_depth":
                                                mDive.setAverageDepth(Double.valueOf(mColumnValue));
                                                break;
                                            case "log_book_no":
                                                mDive.setLogBookNo(Integer.parseInt(mColumnValue));
                                                break;
                                            case "status":
                                                mDive.setStatusLoad(mColumnValue);
                                                break;
                                            case "altitude":
                                                mDive.setAltitude(Integer.parseInt(mColumnValue));
                                                break;
                                            case "location":
                                                if (mColumnValue.equals("0")) {
                                                    mDive.setLocation("");
                                                } else {
                                                    mDive.setLocation(mColumnValue);
                                                }
                                                break;
                                            case "dive_site":
                                                if (mColumnValue.equals("0")) {
                                                    mDive.setDiveSite("");
                                                } else {
                                                    mDive.setDiveSite(mColumnValue);
                                                }
                                                break;
                                            case "dive_boat":
                                                if (mColumnValue.equals("0")) {
                                                    mDive.setDiveBoat("");
                                                } else {
                                                    mDive.setDiveBoat(mColumnValue);
                                                }
                                                break;
                                            case "purpose":
                                                if (mColumnValue.equals("0")) {
                                                    mDive.setPurpose("");
                                                } else {
                                                    mDive.setPurpose(mColumnValue);
                                                }
                                                break;
                                            case "visibility":
                                                if (mColumnValue.equals("0")) {
                                                    mDive.setVisibility("");
                                                } else {
                                                    mDive.setVisibility(mColumnValue);
                                                }
                                                break;
                                            case "maximum_depth":
                                                mDive.setMaximumDepth(Double.valueOf(mColumnValue));
                                                break;
                                            case "suit":
                                                mDive.setSuitLoad(mColumnValue);
                                                break;
                                            case "weight":
                                                mDive.setWeight(Double.valueOf(mColumnValue));
                                                break;
                                            case "air_temp":
                                                mDive.setAirTemp(Double.valueOf(mColumnValue));
                                                break;
                                            case "water_temp_surface":
                                                mDive.setWaterTempSurface(Double.valueOf(mColumnValue));
                                                break;
                                            case "water_temp_bottom":
                                                mDive.setWaterTempBottom(Double.valueOf(mColumnValue));
                                                break;
                                            case "water_temp_average":
                                                mDive.setWaterTempAverage(Double.valueOf(mColumnValue));
                                                break;
                                            case "note_summary":
                                                mDive.setNoteSummary(mColumnValue);
                                                break;
                                            case "environment":
                                                mDive.setEnvironmentLoad(mColumnValue);
                                                break;
                                            case "platform":
                                                mDive.setPlatformLoad(mColumnValue);
                                                break;
                                            case "weather":
                                                mDive.setWeatherLoad(mColumnValue);
                                                break;
                                            case "condition":
                                                mDive.setConditionLoad(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return dives;
    }

    public List<DivePlan> parseDivePlans(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("dive_plan");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DivePlan
                            mDivePlan = new DivePlan();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("dive_plan")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DivePlan object to list
                                    if (mCurrentTableFound) {
                                        divePlans.add(mDivePlan);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "dive_plan_no":
                                                mDivePlan.setDivePlanNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_no":
                                                mDivePlan.setDiveNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "order_no":
                                                mDivePlan.setOrderNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "depth":
                                                mDivePlan.setDepth(Double.valueOf(mColumnValue));
                                                break;
                                            case "minute":
                                                mDivePlan.setMinute(Integer.parseInt(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return divePlans;
    }

    public List<Diver> parseDivers(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("diver");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of Diver
                            mDiver = new Diver();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("diver")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add Diver object to list
                                    if (mCurrentTableFound) {
                                        divers.add(mDiver);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "diver_no":
                                                mDiver.setDiverNo(Long.valueOf(mColumnValue));
                                                break;
                                            case "first_name":
                                                mDiver.setFirstName(mColumnValue);
                                                break;
                                            case "middle_name":
                                                mDiver.setMiddleName(mColumnValue);
                                                break;
                                            case "last_name":
                                                mDiver.setLastName(mColumnValue);
                                                break;
                                            case "gender":
                                                mDiver.setGender(Integer.parseInt(mColumnValue) == 1);
                                                break;
                                            case "birth_date":
                                                // Format looks like 852094800000
                                                mDiver.setBirthDate(MyFunctions.convertDateFromLongToDate(Long.valueOf(mColumnValue)));
                                                break;
                                            case "phone":
                                                mDiver.setPhone(mColumnValue);
                                                break;
                                            case "email":
                                                mDiver.setEmail(mColumnValue);
                                                break;
                                            case "certification_body":
                                                mDiver.setCertificationBody(mColumnValue);
                                                break;
                                            case "certification_level":
                                                mDiver.setCertificationLevel(mColumnValue);
                                                break;
                                            case "max_depth_allowed":
                                                mDiver.setMaxDepthAllowed(Double.valueOf(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return divers;
    }

    public List<DiverDive> parseDiverDives(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("diver_dive");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DiverDive
                            mDiverDive = new DiverDive();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("diver_dive")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add Diver object to list
                                    if (mCurrentTableFound) {
                                        diverDives.add(mDiverDive);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "diver_no":
                                                mDiverDive.setDiverNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_no":
                                                mDiverDive.setDiveNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "rmv":
                                                mDiverDive.setRmv(Double.valueOf(mColumnValue));
                                                break;
                                            case "is_primary":
                                                mDiverDive.setIsPrimary(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return diverDives;
    }

    public List<DiverDiveGroup> parseDiverDiveGroups(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("diver_dive_group");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DiverDiveGroup
                            mDiverDiveGroup = new DiverDiveGroup();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("diver_dive_group")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiverDiveGroup object to list
                                    if (mCurrentTableFound) {
                                        diverDiveGroups.add(mDiverDiveGroup);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "diver_no":
                                                mDiverDiveGroup.setDiverNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_no":
                                                mDiverDiveGroup.setDiveNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "group_no":
                                                mDiverDiveGroup.setGroupNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "sac":
                                                mDiverDiveGroup.setSac(Double.valueOf(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return diverDiveGroups;
    }

    public List<DiverDiveGroupCyl> parseDiverDiveGroupCyls(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("diver_dive_group_cylinder");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DiverDiveGroupCyl
                            mDiverDiveGroupCyl = new DiverDiveGroupCyl();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("diver_dive_group_cylinder")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiverDiveGroupCyl object to list
                                    if (mCurrentTableFound) {
                                        diverDiveGroupCyls.add(mDiverDiveGroupCyl);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "diver_no":
                                                mDiverDiveGroupCyl.setDiverNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_no":
                                                mDiverDiveGroupCyl.setDiveNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "group_no":
                                                mDiverDiveGroupCyl.setGroupNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "cylinder_no":
                                                mDiverDiveGroupCyl.setCylinderNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "beginning_pressure":
                                                mDiverDiveGroupCyl.setBeginningPressure(Double.valueOf(mColumnValue));
                                                break;
                                            case "ending_pressure":
                                                mDiverDiveGroupCyl.setEndingPressure(Double.valueOf(mColumnValue));
                                                break;
                                            case "o2":
                                                mDiverDiveGroupCyl.setO2(Integer.parseInt(mColumnValue));
                                                break;
                                            case "n":
                                                mDiverDiveGroupCyl.setN(Integer.parseInt(mColumnValue));
                                                break;
                                            case "he":
                                                mDiverDiveGroupCyl.setHe(Integer.parseInt(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return diverDiveGroupCyls;
    }

    public List<DiveSegment> parseDiveSegments(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("dive_segment");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DiveSegment
                            mDiveSegment = new DiveSegment();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("dive_segment")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiveSegment object to list
                                    if (mCurrentTableFound) {
                                        diveSegments.add(mDiveSegment);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "diver_no":
                                                mDiveSegment.setDiverNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_no":
                                                mDiveSegment.setDiveNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "segment_type":
                                                mDiveSegment.setSegmentType(mColumnValue);
                                                break;
                                            case "order_no":
                                                mDiveSegment.setOrderNo(Integer.parseInt(mColumnValue));
                                                break;
                                            case "depth":
                                                mDiveSegment.setDepth(Double.valueOf(mColumnValue));
                                                break;
                                            case "air_consumption_pressure":
                                                mDiveSegment.setAirConsumptionPressure(Double.valueOf(mColumnValue));
                                                break;
                                            case "air_consumption_volume":
                                                mDiveSegment.setAirConsumptionVolume(Double.valueOf(mColumnValue));
                                                break;
                                            case "calc_ata":
                                                mDiveSegment.setCalcAta(Double.valueOf(mColumnValue));
                                                break;
                                            case "calc_average_depth":
                                                mDiveSegment.setCalcAverageDepth(Double.valueOf(mColumnValue));
                                                break;
                                            case "calc_average_ata":
                                                mDiveSegment.setCalcAverageAta(Double.valueOf(mColumnValue));
                                                break;
                                            case "calc_descent_rate":
                                                mDiveSegment.setCalcDescentRate(Integer.parseInt(mColumnValue));
                                                break;
                                            case "calc_ascent_rate":
                                                mDiveSegment.setCalcAscentRate(Integer.parseInt(mColumnValue));
                                                break;
                                            case "calc_decreasing_pressure":
                                                mDiveSegment.setCalcDecreasingPressure(Double.valueOf(mColumnValue));
                                                break;
                                            case "calc_decreasing_volume":
                                                mDiveSegment.setCalcDecreasingVolume(Double.valueOf(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return diveSegments;
    }

    public List<DiveType> parseDiveTypes(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("dive_type");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DiveType
                            mDiveType = new DiveType();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("dive_type")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiveType object to list
                                    if (mCurrentTableFound) {
                                        diveTypes.add(mDiveType);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "dive_type":
                                                mDiveType.setDiveType(mColumnValue);
                                                break;
                                            case "description":
                                                mDiveType.setDescription(mColumnValue);
                                                break;
                                            case "sort_order":
                                                mDiveType.setSortOrder(Integer.valueOf(mColumnValue));
                                                break;
                                            case "in_picker":
                                                mDiveType.setInPicker(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return diveTypes;
    }

    public List<DynamicSpinner> parseDynamicSpinners(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("dynamic_spinner");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of DynamicSpinner
                            mDynamicSpinner= new DynamicSpinner();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("dynamic_spinner")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add Diver object to list
                                    if (mCurrentTableFound) {
                                        dynamicSpinners.add(mDynamicSpinner);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "system_defined":
                                                mDynamicSpinner.setSystemDefined(mColumnValue);
                                                break;
                                            case "spinner_type":
                                                mDynamicSpinner.setSpinnerType(mColumnValue);
                                                break;
                                            case "spinner_text":
                                                mDynamicSpinner.setSpinnerText(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return dynamicSpinners;
    }

    public List<Groupp> parseGroupps(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("groupp");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of Groupp
                            mGroupp = new Groupp();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("groupp")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiveType object to list
                                    if (mCurrentTableFound) {
                                        groupps.add(mGroupp);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "group_no":
                                                mGroupp.setGroupNo(Long.valueOf(mColumnValue));
                                                break;
                                            case "diver_no":
                                                mGroupp.setDiverNo(Long.valueOf(mColumnValue));
                                                break;
                                            case "group_type":
                                                mGroupp.setGroupTypeLoad(mColumnValue);
                                                break;
                                            case "description":
                                                mGroupp.setDescription(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return groupps;
    }

    public List<GrouppCylinder> parseGrouppCylinders(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("group_cylinder");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of GrouppCylinder
                            mGrouppCylinder = new GrouppCylinder();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("group_cylinder")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiveType object to list
                                    if (mCurrentTableFound) {
                                        grouppCylinders.add(mGrouppCylinder);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "group_no":
                                                mGrouppCylinder.setGroupNo(Long.valueOf(mColumnValue));
                                                break;
                                            case "cylinder_no":
                                                mGrouppCylinder.setCylinderNo(Long.valueOf(mColumnValue));
                                                break;
                                            case "usage_type":
                                                mGrouppCylinder.setUsageTypeLoad(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return grouppCylinders;
    }

    public List<GrouppType> parseGrouppTypes(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("group_type");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of GrouppType
                            mGrouppType = new GrouppType();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("group_type")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiveType object to list
                                    if (mCurrentTableFound) {
                                        grouppTypes.add(mGrouppType);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "group_type":
                                                mGrouppType.setGroupType(mColumnValue);
                                                break;
                                            case "description":
                                                mGrouppType.setDescription(mColumnValue);
                                                break;
                                            case "system_defined":
                                                mGrouppType.setSystemDefined(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return grouppTypes;
    }

    public List<SegmentType> parseSegmentTypes(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("segment_type");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of SegmentType
                            mSegmentType = new SegmentType();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("segment_type")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add DiveType object to list
                                    if (mCurrentTableFound) {
                                        segmentTypes.add(mSegmentType);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "segment_type":
                                                mSegmentType.setSegmentType(mColumnValue);
                                                break;
                                            case "description":
                                                mSegmentType.setDescription(mColumnValue);
                                                break;
                                            case "order_no":
                                                mSegmentType.setOrderNo(Integer.parseInt(mColumnValue));
                                                break;
                                            case "direction":
                                                mSegmentType.setDirection(mColumnValue);
                                                break;
                                            case "show_result":
                                                mSegmentType.setShowResult(mColumnValue);
                                                break;
                                            case "system_defined":
                                                mSegmentType.setSystemDefined(mColumnValue);
                                                break;
                                            case "status":
                                                mSegmentType.setStatus(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return segmentTypes;
    }

    public List<Setting> parseSettings(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("setting");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of Setting
                            mSetting = new Setting();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("setting")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add Setting object to list
                                    if (mCurrentTableFound) {
                                        settings.add(mSetting);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        mSetting.setColumnName(mColumnName);
                                        mSetting.setColumnValue(mColumnValue);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public List<State> parseStates(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("state");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of State
                            mState = new State();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("state")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add State object to list
                                    if (mCurrentTableFound) {
                                        states.add(mState);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "state_no":
                                                mState.setStateNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "dive_type":
                                                mState.setDiveType(mColumnValue);
                                                break;
                                            case "my_buddy_diver_no":
                                                mState.setBuddyDiverNo(Long.parseLong(mColumnValue));
                                                break;
                                            case "my_sac":
                                                mState.setMySac(Double.valueOf(mColumnValue));
                                                break;
                                            case "my_rmv":
                                                mState.setMyRmv(Double.valueOf(mColumnValue));
                                                break;
                                            case "my_group":
                                                mState.setMyGroup(Long.parseLong(mColumnValue));
                                                break;
                                            case "my_buddy_sac":
                                                mState.setMyBuddySac(Double.valueOf(mColumnValue));
                                                break;
                                            case "my_buddy_rmv":
                                                mState.setMyBuddyRmv(Double.valueOf(mColumnValue));
                                                break;
                                            case "my_buddy_group":
                                                mState.setMyBuddyGroup(Long.parseLong(mColumnValue));
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return states;
    }

    public List<UsageType> parseUsageTypes(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            mCurrentTableFound = false;
            mEndProcess = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !mEndProcess) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 2
                        if (tagName.equalsIgnoreCase("table")) {
                            mTableName = parser.getAttributeValue(null, "name");
                            mCurrentTableFound = mTableName.equalsIgnoreCase("usage_type");
                        } else if (tagName.equalsIgnoreCase("row") && mCurrentTableFound) {
                            // Create a new instance of UsageType
                            mUsageType = new UsageType();
                        } else if (tagName.equalsIgnoreCase("col") && mCurrentTableFound) {
                            mColumnName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT: // 4
                        mColumnValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG: // 3
                        try{
                            switch (tagName.toLowerCase(Locale.ROOT)) {
                                case "table":
                                    if (mTableName.equalsIgnoreCase("usage_type")) {
                                        mCurrentTableFound = false;
                                        mEndProcess = true;
                                    }
                                    break;
                                case "row":
                                    // Add UsageType object to list
                                    if (mCurrentTableFound) {
                                        usageTypes.add(mUsageType);
                                    }
                                    break;
                                case "col":
                                    if (mCurrentTableFound) {
                                        switch (mColumnName.toLowerCase(Locale.ROOT)) {
                                            case "usage_type":
                                                mUsageType.setUsageType(mColumnValue);
                                                break;
                                            case "description":
                                                mUsageType.setDescription(mColumnValue);
                                                break;
                                            case "system_defined":
                                                mUsageType.setSystemDefined(mColumnValue);
                                                break;
                                        }
                                        // XML does not always have a text value for an empty column
                                        // Reset the column value to empty
                                        mColumnValue = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return usageTypes;
    }
}
