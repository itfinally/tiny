package top.itfinally.core.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Api View Entity
 *
 * Json format is as follows:
 *  name:           {method name}
 *  fullName:       {method name including class path}
 *  description:    {the method description}
 *  urls:           {
 *                      "http method1": [ url1, url2, ... ],
 *                      "http method2": [ url1, url2, ... ],
 *                      ...
 *                  }
 *  args:           {
 *                      "arg name1": "arg type",
 *                      "arg name2": "arg type",
 *                      ...
 *                  }
 */
public class ApiViewVoBean {
    private String name;
    private String fullName;
    private String description;

    private Map<String, List<String>> urls = new HashMap<>();
    private Map<String, String> args = new HashMap<>();

    public String getName() {
        return name;
    }

    public ApiViewVoBean setName( String name ) {
        this.name = name;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public ApiViewVoBean setFullName( String fullName ) {
        this.fullName = fullName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApiViewVoBean setDescription( String description ) {
        this.description = description;
        return this;
    }

    public Map<String, List<String>> getUrls() {
        return urls;
    }

    public ApiViewVoBean setUrls( Map<String, List<String>> urls ) {
        this.urls = urls;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public ApiViewVoBean setArgs( Map<String, String> args ) {
        this.args = args;
        return this;
    }

    @Override
    public String toString() {
        return "ApiViewVoBean{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", description='" + description + '\'' +
                ", urls=" + urls +
                ", args=" + args +
                '}';
    }
}
