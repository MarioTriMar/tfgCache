package org.tfg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan
public class TfgCacheApplication extends SpringBootServletInitializer
{
    public static void main( String[] args )
    {
        SpringApplication.run(TfgCacheApplication.class, args);
    }
}
