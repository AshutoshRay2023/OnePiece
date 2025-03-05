package com.designpattern.Builder;


public class URL {
    public static void main(String[] args) {
        URLBuilder urlBuilder=new URLBuilder.Builder()
                .setProtocol("https")
                .setHostname("google.com")
                .setPort("8080")
                .setPathparam("images")
                .setQueryparam("companyName=xyz")
                .build();
        System.out.println(urlBuilder);
    }
}


/**
 * package com.designpattern.builder;
 *
 * public class URLBuilder {
 *     private final String protocol;
 *     private final String hostname;
 *     private final String port;
 *     private final String pathParam;
 *     private final String queryParam;
 *
 *     private URLBuilder(Builder builder) {
 *         this.protocol = builder.protocol;
 *         this.hostname = builder.hostname;
 *         this.port = builder.port;
 *         this.pathParam = builder.pathParam;
 *         this.queryParam = builder.queryParam;
 *     }
 *
 *     public String getURL() {
 *         StringBuilder url = new StringBuilder();
 *         if (protocol != null) url.append(protocol).append("://");
 *         if (hostname != null) url.append(hostname);
 *         if (port != null) url.append(":").append(port);
 *         if (pathParam != null) url.append("/").append(pathParam);
 *         if (queryParam != null) url.append("?").append(queryParam);
 *         return url.toString();
 *     }
 *
 *     @Override
 *     public String toString() {
 *         return getURL();
 *     }
 *
 *     public static class Builder {
 *         private String protocol;
 *         private String hostname;
 *         private String port;
 *         private String pathParam;
 *         private String queryParam;
 *
 *         public Builder protocol(String protocol) {
 *             this.protocol = protocol;
 *             return this;
 *         }
 *
 *         public Builder hostname(String hostname) {
 *             this.hostname = hostname;
 *             return this;
 *         }
 *
 *         public Builder port(String port) {
 *             this.port = port;
 *             return this;
 *         }
 *
 *         public Builder pathParam(String pathParam) {
 *             this.pathParam = pathParam;
 *             return this;
 *         }
 *
 *         public Builder queryParam(String queryParam) {
 *             this.queryParam = queryParam;
 *             return this;
 *         }
 *
 *         public URLBuilder build() {
 *             if (hostname == null || hostname.isEmpty()) {
 *                 throw new IllegalStateException("Hostname cannot be null or empty");
 *             }
 *             return new URLBuilder(this);
 *         }
 *     }
 *
 *     public static void main(String[] args) {
 *         URLBuilder url = new URLBuilder.Builder()
 *                 .protocol("https")
 *                 .hostname("mywebsite.com")
 *                 .port("8080")
 *                 .pathParam("companies")
 *                 .queryParam("companyName=xyz")
 *                 .build();
 *
 *         System.out.println(url);
 *     }
 * }
 */