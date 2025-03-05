package designpattern.Builder;


/**
 https://mywebsite:8080/companies?companyName=xyz
 |         |   |         |
 protocol Hostname Port PathParam Query Param
 **/


public class URLBuilder {
    private final String protocol;
    private final String hostname;
    private final String port;
    private final String pathparam;
    private final String queryparam;

    private URLBuilder(Builder builder){
        this.protocol=builder.protocol;
        this.hostname=builder.hostname;
        this.port=builder.port;
        this.pathparam=builder.pathparam;
        this.queryparam=builder.queryparam;
    }
    public String getProtocol() {
        return protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getPathparam() {
        return pathparam;
    }

    public String getQueryparam() {
        return queryparam;
    }

    @Override
    public String toString() {
        return "URLBuilder{" +
                "protocol='" + protocol + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                ", pathparam='" + pathparam + '\'' +
                ", queryparam='" + queryparam + '\'' +
                '}';
    }

    public String getURL() {
        StringBuilder url = new StringBuilder();
        if (protocol != null) url.append(protocol).append("://");
        if (hostname != null) url.append(hostname);
        if (port != null) url.append(":").append(port);
        if (pathparam != null) url.append("/").append(pathparam);
        if (queryparam != null) url.append("?").append(queryparam);
        return url.toString();
    }


    public static class Builder{
        private String protocol;
        private String hostname;
        private String port;
        private String pathparam;
        private String queryparam;

        public Builder setProtocol(String protocol){
            this.protocol=protocol;
            return this;
        }

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Builder setPathparam(String pathparam) {
            this.pathparam = pathparam;
            return this;
        }

        public Builder setQueryparam(String queryparam) {
            this.queryparam = queryparam;
            return this;
        }

        public URLBuilder build(){
            return new URLBuilder(this);
        }
    }
}
