package org.example.eureka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EurekaApplications
{
    private Applications applications;

    public Applications getApplications ()
    {
        return applications;
    }

    public void setApplications (Applications applications)
    {
        this.applications = applications;
    }

    @Override
    public String toString()
    {
        return "[applications = "+applications+"]";
    }
}