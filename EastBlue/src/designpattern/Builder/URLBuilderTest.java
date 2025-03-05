package designpattern.Builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class URLBuilderTest {

    @Test
    public void testValidURL(){
        URLBuilder url=new URLBuilder.Builder().setProtocol("https")
                .setHostname("example.com")
                .setPort("8080")
                .setPathparam("api")
                .setQueryparam("key=value")
                .build();
        assertEquals("https://example.com:8080/api?key=value",url.getURL());
    }
}
