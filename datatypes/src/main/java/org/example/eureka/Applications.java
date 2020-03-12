package org.example.eureka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Applications
{
    private Application[] application;

    public Application[] getApplication ()
    {
        return application;
    }

    public void setApplication (Application[] application)
    {
        this.application = application;
    }

    @Override
    public String toString()
    {
        return "[application = "+application+"]";
    }
}