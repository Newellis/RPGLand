package com.tynellis.Save;

import com.tynellis.GameComponent;
import com.tynellis.Save.OSLib.EnumOS2;
import com.tynellis.Save.OSLib.EnumOSMappingHelper;
import com.tynellis.World.world_parts.Regions.Region;
import com.tynellis.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHandler {
    public static final String extension = ".txt";
    private static volatile File AppLibDir = null;
    private static File GameDir = null;
    private static File CharDir = null;

    //Check is Save str exists
    public static boolean checkGameDir(String str) {
        File game = new File(getAppLibDir(), str);
        File world = new File(game,"world" + extension);
        return world.exists();
    }
    //set the active GameDir and create it if needed
    public static void setGameDir(String str){
        GameDir = new File(getAppLibDir(), str);
        CharDir = new File(GameDir, "Characters");
        if (!GameDir.exists()){
            GameDir.mkdir();
        }
        if (!CharDir.exists()){
            CharDir.mkdir();
        }
    }

    //get active Games Character Directory
    public static File getCharDir() {
        return CharDir;
    }

    //get active Game Directory
    public static File getGameDir() {
        return GameDir;
    }

    public static File getRegionDir(Region region) {
        File RegionDir = new File(GameDir, region.getName());
        if (!RegionDir.exists()) {
            RegionDir.mkdir();
        }
        return RegionDir;
    }

    //get the Games Directory in the App Directory
    public static File getAppLibDir() {
        if (AppLibDir == null) {
            AppLibDir = getAppDir("RPGLand");//@todo change name at some point
        }
        return AppLibDir;
    }


    //figure out the operating system
    private static EnumOS2 getOs() {
        String s = System.getProperty("os.name").toLowerCase();
        if (s.contains("win")) {
            return EnumOS2.windows;
        }else if (s.contains("mac")) {
            return EnumOS2.macOS;
        }else if (s.contains("solaris") || s.contains("sunos")) {
            return EnumOS2.solaris;
        }else if (s.contains("linux")|| s.contains("unix")) {
            return EnumOS2.linux;
        } else {
            return EnumOS2.unknown;
        }
    }

    //get the App Directory based on operating system
    private static File getAppDir(String s) {
        String s1 = System.getProperty("user.home", ".");
        File file;
        switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()]) {
            case 1: // '\001'
            case 2: // '\002'
                file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
                break;

            case 3: // '\003'
                String s2 = System.getenv("APPDATA");
                if (s2 != null) {
                    file = new File(s2, (new StringBuilder()).append(".").append(s).append('/').toString());
                } else {
                    file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
                }
                break;

            case 4: // '\004'
                file = new File(s1, (new StringBuilder()).append("Library/Application Support/").append(s).toString());
                break;

            default:
                file = new File(s1, (new StringBuilder()).append(s).append('/').toString());
                break;
        }
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException((new StringBuilder()).append("The working directory could not be created: ").append(file).toString());
        } else {
            return file;
        }
    }

    //store Object o in File file
    public static void store(Object o, File file) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(o);
            out.close();
            fileOut.close();
            if (GameComponent.debug.isType(Debug.Type.SAVE)) {//@todo change to prevent corruption if interrupted
                System.out.println("Serialized data is saved in " + file.getPath());
            }
        } catch(IOException i){
            i.printStackTrace();
        }
    }

    //load an object from File file
    public static Object load(File file) {
        try {
            Object o = null;
            if (GameComponent.debug.isType(Debug.Type.SAVE)) {
                System.out.println("Serialized data is read in from " + file.getPath());
            }
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            o = in.readObject();
            in.close();
            fileIn.close();
            return o;
        }catch(FileNotFoundException i){
            if (GameComponent.debug.isType(Debug.Type.SAVE)) {
                System.out.println("File not found");
            }
        }catch(IOException i) {
            i.printStackTrace();
        } catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
        //System.exit(1);
        return null; // not reached
    }
}
