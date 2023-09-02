package ngit.maker.recorder.workflow;

import ngit.maker.recorder.keyboards.GlobalKeyListener;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowSaver {
    public static final String MAKER_APPLICATION_PORT = System.getenv("LOCALAPPDATA") + "\\MakerApps";
    public static final String THIS_SETTINGS_TAG = "WorkflowManager";

    private final String STORE_PLACE_KEY = "WFM-Default store place:";
    private final String STORE_PLACE_DEFAULT = MAKER_APPLICATION_PORT + "\\WorkflowMgr\\saves";
    private final String STORE_PASSWORD_KEY = "WFM-You know it:";
    private final String STORE_PASSWORD_DEFAULT = "1145141919810";

    private final String STORE_MINIMIZE_KEYBOARD_KEY = "Minimize hotkey: ";
    private final String STORE_MINIMIZE_KEYBOARD_DEFAULT = "CONTROL_SHIFT_NULL_F7";
    private final String STORE_MINIMIZE_AVAILABLE_KEY = "Minimize available: ";
    private final String STORE_MINIMIZE_AVAILABLE_DEFAULT = "true";

    private final String STORE_UNDECORATED_KEYBOARD_KEY = "Undecorated hotkey: ";
    private final String STORE_UNDECORATED_KEYBOARD_DEFAULT = "CONTROL_SHIFT_NULL_F9";
    private final String STORE_UNDECORATED_AVAILABLE_KEY = "Undecorated available: ";
    private final String STORE_UNDECORATED_AVAILABLE_DEFAULT = "true";

    private final String STORE_EXIT_KEYBOARD_KEY = "Exit hotkey: ";
    private final String STORE_EXIT_KEYBOARD_DEFAULT = "CONTROL_SHIFT_NULL_F8";
    private final String STORE_EXIT_AVAILABLE_KEY = "Exit available: ";
    private final String STORE_EXIT_AVAILABLE_DEFAULT = "true";

    private final String STORE_SAVE_KEYBOARD_KEY = "Save hotkey: ";
    private final String STORE_SAVE_KEYBOARD_DEFAULT = "CONTROL_NULL_NULL_S";
    private final String STORE_SAVE_AVAILABLE_KEY = "Save available: ";
    private final String STORE_SAVE_AVAILABLE_DEFAULT = "true";

    private final Logger logger;
    private final Properties properties;

    private enum PropertiesExistKnockBack {
        TRULY_EXIST,
        CANT_READ,
        CANT_WRITE,
        NOTHING_IN,
        NOT_CORRECT,
        NOT_EXIST
    }

    public interface ReadSupplier {
        String getObj();

        boolean isValid();

        static ReadSupplier returnValidOne(final String string) {
            return new ReadSupplier() {
                @Override
                public String getObj() {
                    return string;
                }

                @Override
                public boolean isValid() {
                    return true;
                }
            };
        }

        static ReadSupplier returnInvalidOne(final String string) {
            return new ReadSupplier() {
                @Override
                public String getObj() {
                    return string;
                }

                @Override
                public boolean isValid() {
                    return false;
                }
            };
        }
    }

    public WorkflowSaver(Logger logger) {
        this.logger = logger;
        this.properties = new Properties();
    }

    public boolean createPropertiesFile() throws IOException {
        File file = new File(MAKER_APPLICATION_PORT + "\\settings.properties");
        return createDirAndFile(file);
    }

    public boolean createDirAndFile(File file) throws IOException {
        if (!file.exists()) {
            if (file.isDirectory() && !file.exists()) return file.mkdirs();
            else if (!file.getParentFile().exists()) return file.getParentFile().mkdirs() && file.createNewFile();
            else return file.createNewFile();
        } else return false;
    }

    private PropertiesExistKnockBack readPropertiesCheck(Logger logger) {
        try {
            File file = new File(MAKER_APPLICATION_PORT + "\\settings.properties");
            if (!file.exists()) throw new NullPointerException("I have to throw it, to reach the solution.");
            if (!file.canRead()) throw new IOException("I have to throw it, to reach the solution.");

            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
                return properties.propertyNames() == null ?
                        PropertiesExistKnockBack.NOTHING_IN : PropertiesExistKnockBack.TRULY_EXIST;
            }

        } catch (NullPointerException e) {
            return PropertiesExistKnockBack.NOT_EXIST;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception at reading user profile: ", e);
            return PropertiesExistKnockBack.CANT_READ;
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "User profile is not correct: ", e);
            return PropertiesExistKnockBack.NOT_CORRECT;
        }
    }


    public ReadSupplier readProperty(final String key, final String defaultKey) {
        PropertiesExistKnockBack knockBack = readPropertiesCheck(logger);

        if (knockBack.equals(PropertiesExistKnockBack.NOT_CORRECT)) {
            logger.log(Level.INFO, "Properties file not correct, try to refresh.");
            if (refreshProperties()) {
                logger.log(Level.INFO, "Successfully refresh properties file.");
            } else {
                logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                return ReadSupplier.returnInvalidOne(defaultKey);
            }
        } else if (knockBack.equals(PropertiesExistKnockBack.NOT_EXIST)) {
            try {
                if (createPropertiesFile()) {
                    logger.log(Level.INFO, "Successfully create a settings.properties file.");
                } else throw new RuntimeException("I cant create properties file! Is that you? (TAT)");
                if (refreshProperties()) {
                    logger.log(Level.INFO, "Successfully refresh properties file.");
                } else {
                    logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                    return ReadSupplier.returnInvalidOne(defaultKey);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't create new user profile: ", e);
                return ReadSupplier.returnInvalidOne(defaultKey);
            }
        } else if (knockBack.equals(PropertiesExistKnockBack.CANT_READ) || knockBack.equals(PropertiesExistKnockBack.CANT_WRITE)) {
            return ReadSupplier.returnInvalidOne(defaultKey);
        } else if (knockBack.equals(PropertiesExistKnockBack.NOTHING_IN)) {
            if (refreshProperties()) {
                logger.log(Level.INFO, "Successfully refresh properties file.");
            } else {
                logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                return ReadSupplier.returnInvalidOne(defaultKey);
            }
        }
        return ReadSupplier.returnValidOne(properties.getProperty(key, defaultKey));
    }

    private boolean commitChange() {
        try {
            File file = new File(MAKER_APPLICATION_PORT + "\\settings.properties");
            if (!file.exists()) throw new NullPointerException("(???) Where's my properties?!");
            if (!file.canWrite()) throw new IOException("I can't write in things! Let me in!!! (TAT)");

            try (OutputStream outputStream = new FileOutputStream(file)) {
                properties.store(outputStream, THIS_SETTINGS_TAG);
                return true;
            }

        } catch (NullPointerException e) {
            try {
                if (createPropertiesFile()) {
                    logger.log(Level.INFO, "Successfully create a settings.properties file.");
                } else throw new RuntimeException("I cant create properties file! Is that you? (TAT)");
                if (refreshProperties()) {
                    logger.log(Level.INFO, "Successfully refresh properties file.");
                } else {
                    logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                    return false;
                }
                return true;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Can't create new user profile: ", ex);
            }
            return false;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception at writing user profile: ", e);
            return false;
        }
    }


    public boolean setStorePlace(final String newPlace) {
        properties.setProperty(STORE_PLACE_KEY, newPlace);
        return commitChange();
    }
    public ReadSupplier readStorePlace() {
        return readProperty(STORE_PLACE_KEY, STORE_PLACE_DEFAULT);
    }


    public boolean setStorePlaintextPassword(final String newPassword) {
        properties.setProperty(STORE_PASSWORD_KEY, newPassword);
        return commitChange();
    }
    public ReadSupplier readStorePlaintextPassword() {
        return readProperty(STORE_PASSWORD_KEY, STORE_PASSWORD_DEFAULT);
    }


    public boolean setMinimizeHotkey(final String newKeyArray) {
        properties.setProperty(STORE_MINIMIZE_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readMinimizeHotkey() {
        return readProperty(STORE_MINIMIZE_KEYBOARD_KEY, STORE_MINIMIZE_KEYBOARD_DEFAULT);
    }
    public boolean setMinimizeAvailable(final boolean available) {
        properties.setProperty(STORE_MINIMIZE_AVAILABLE_KEY, String.valueOf(available));
        return commitChange();
    }
    public ReadSupplier readMinimizeAvailable() {
        return readProperty(STORE_MINIMIZE_AVAILABLE_KEY, STORE_MINIMIZE_AVAILABLE_DEFAULT);
    }


    public boolean setUndecoratedHotkey(final String newKeyArray) {
        properties.setProperty(STORE_UNDECORATED_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readUndecoratedHotkey() {
        return readProperty(STORE_UNDECORATED_KEYBOARD_KEY, STORE_UNDECORATED_KEYBOARD_DEFAULT);
    }
    public boolean setUndecoratedAvailable(final boolean available) {
        properties.setProperty(STORE_UNDECORATED_AVAILABLE_KEY, String.valueOf(available));
        return commitChange();
    }
    public ReadSupplier readUndecoratedAvailable() {
        return readProperty(STORE_UNDECORATED_AVAILABLE_KEY, STORE_UNDECORATED_AVAILABLE_DEFAULT);
    }


    public boolean setExitHotkey(final String newKeyArray) {
        properties.setProperty(STORE_EXIT_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readExitHotkey() {
        return readProperty(STORE_EXIT_KEYBOARD_KEY, STORE_EXIT_KEYBOARD_DEFAULT);
    }
    public boolean setExitAvailable(final boolean available) {
        properties.setProperty(STORE_EXIT_AVAILABLE_KEY, String.valueOf(available));
        return commitChange();
    }
    public ReadSupplier readExitAvailable() {
        return readProperty(STORE_EXIT_AVAILABLE_KEY, STORE_EXIT_AVAILABLE_DEFAULT);
    }


    public boolean setSaveHotkey(final String newKeyArray) {
        properties.setProperty(STORE_SAVE_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readSaveHotkey() {
        return readProperty(STORE_SAVE_KEYBOARD_KEY, STORE_SAVE_KEYBOARD_DEFAULT);
    }
    public boolean setSaveAvailable(final boolean available) {
        properties.setProperty(STORE_SAVE_AVAILABLE_KEY, String.valueOf(available));
        return commitChange();
    }
    public ReadSupplier readSaveAvailable() {
        return readProperty(STORE_SAVE_AVAILABLE_KEY, STORE_SAVE_AVAILABLE_DEFAULT);
    }


    public boolean refreshProperties() {
        properties.setProperty(STORE_PLACE_KEY, STORE_PLACE_DEFAULT);
        properties.setProperty(STORE_PASSWORD_KEY, STORE_PASSWORD_DEFAULT);

        properties.setProperty(STORE_MINIMIZE_KEYBOARD_KEY, STORE_MINIMIZE_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_MINIMIZE_AVAILABLE_KEY, STORE_MINIMIZE_AVAILABLE_DEFAULT);

        properties.setProperty(STORE_UNDECORATED_KEYBOARD_KEY, STORE_UNDECORATED_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_UNDECORATED_AVAILABLE_KEY, STORE_UNDECORATED_AVAILABLE_DEFAULT);

        properties.setProperty(STORE_EXIT_KEYBOARD_KEY, STORE_EXIT_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_EXIT_AVAILABLE_KEY, STORE_EXIT_AVAILABLE_DEFAULT);

        properties.setProperty(STORE_SAVE_KEYBOARD_KEY, STORE_SAVE_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_SAVE_AVAILABLE_KEY, STORE_SAVE_AVAILABLE_DEFAULT);

        return commitChange();
    }

    public void saveTasks(List<WorkflowRecorderPane> recorders, String startTime) throws IOException {
        ReadSupplier supplier = readStorePlace();
        if (!supplier.isValid()) {
            throw new IOException("Can't read save place, please check if sth went wrong before this error");
        }
        File path = new File(readStorePlace().getObj() + File.separator + startTime);
        File iteratorFile;

        for (WorkflowRecorderPane recorder : recorders) {

            iteratorFile = new File(path.getAbsolutePath() + File.separator + recorder.getCreatedTime() + ".rot");
            try {
                if (!iteratorFile.exists()) {
                    if (!createDirAndFile(iteratorFile))
                        throw new IOException("I can't create file record for your new task, sorry(TwT)");
                }

                try (FileWriter fileWriter = new FileWriter(iteratorFile)) {
                    fileWriter.write("");
                    fileWriter.write(recorder.toString());
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't save work record \"" + recorder.getCreatedTime() + "\" task:", e);
            }
        }
    }

    public List<WorkflowRecorderPane> readTasks(List<WorkflowRecorderPane> recorders, String startTime) {
        File path = new File(readStorePlace().getObj() + File.separator + startTime);

        for (File recorderFile : Objects.requireNonNull(path.listFiles())) {
            try {
                if (!recorderFile.canRead())
                    throw new IOException("I can't read file record from your saves, sorry(TwT)");
                Files.readAllBytes(recorderFile.toPath());
                WorkflowRecorderPane pane = new WorkflowRecorderPane(recorders, recorderFile.getName());
                recorders.add(pane);
                pane.flushInterface(recorders);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't read work record \"" + recorderFile.getName() + "\" task:", e);
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "Can't find file as collecting:", e);
            }
        }
        return recorders;
    }

    public GlobalKeyListener.IKeyPack translateKeys(String keys, GlobalKeyListener.KEY_TAGS forType) {
        String[] kSeparated = keys.split("_");
        if (kSeparated.length != 4){
            logger.log(Level.SEVERE, "Wrong keys has been read: ", new Exception());
        }
        String mod1 = kSeparated[0];
        String mod2 = kSeparated[1];
        String mod3 = kSeparated[2];
        String apartKey = kSeparated[3];
        System.out.println(apartKey);
        GlobalKeyListener.IKeyPack keyPack = GlobalKeyListener.IKeyPack.newPack(forType, mod1, mod2, mod3, apartKey);
        try {
            keyPack.translateAllMods();
            keyPack.translateKey(apartKey);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Illegal keys were found, using default one.", e);
            kSeparated = switch (forType){
                case MINIMIZE_KEY_MARK -> STORE_MINIMIZE_KEYBOARD_DEFAULT.split("_");
                case UNDECORATED_KEY_MARK -> STORE_UNDECORATED_KEYBOARD_DEFAULT.split("_");
                case EXIT_ALL_KEY_MARK -> STORE_EXIT_KEYBOARD_DEFAULT.split("_");
                case SAVE_KEY_MARK -> STORE_SAVE_KEYBOARD_DEFAULT.split("_");
            };
            keyPack = GlobalKeyListener.IKeyPack.newPack(forType, kSeparated[0], kSeparated[1], kSeparated[2], kSeparated[3]);
        }

        return keyPack;
    }
}