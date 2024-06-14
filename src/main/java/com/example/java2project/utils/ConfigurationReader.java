package com.example.java2project.utils;

import com.example.java2project.config.ConfigurationKey;
import com.example.java2project.exception.InvalidConfigurationKeyException;
import com.example.java2project.jndi.InitialDirContextCloseable;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {
    private static  ConfigurationReader reader =null;

    private static final Hashtable<String, String> environment;

    static {
        environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL,"file:C:/configuration");
    }

    public static Integer readIntegerConfigurationValueForKey(ConfigurationKey key){

      String value = readStringConfigurationValueForKey(key);
      return Integer.parseInt(value);
    }

    public static String readStringConfigurationValueForKey(ConfigurationKey key){

        try(InitialDirContextCloseable context = new InitialDirContextCloseable(environment))
        {
            return readValueForKey(context,key);
        } catch ( NamingException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String readValueForKey(Context context,ConfigurationKey key)
    {
        String fileName= "conf.properties";
        try {
            Object object =context.lookup(fileName);
            Properties props = new Properties();
            props.load(new FileReader(object.toString()));
            String value = props.getProperty(key.getKeyName());
            if (value==null)
            {
                throw new InvalidConfigurationKeyException("The config key '"+key.getKeyName()+"'does not exist");
            }
            return props.getProperty(key.getKeyName());
        }
        catch (NamingException| IOException ex)
        {
            throw new RuntimeException("The config can't be read!",ex);
        }
    }
}
