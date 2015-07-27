package com.tynellis.Save.OSLib;

public class EnumOSMappingHelper {
    public static final int enumOSMappingArray[]; /* synthetic field */

    static
    {
        enumOSMappingArray = new int[EnumOS2.values().length];
        try
        {
            enumOSMappingArray[EnumOS2.linux.ordinal()] = 1;
        }
        catch(NoSuchFieldError ignored) { }
        try
        {
            enumOSMappingArray[EnumOS2.solaris.ordinal()] = 2;
        }
        catch(NoSuchFieldError ignored) { }
        try
        {
            enumOSMappingArray[EnumOS2.windows.ordinal()] = 3;
        }
        catch(NoSuchFieldError ignored) { }
        try
        {
            enumOSMappingArray[EnumOS2.macOS.ordinal()] = 4;
        }
        catch(NoSuchFieldError ignored) { }
    }
}
