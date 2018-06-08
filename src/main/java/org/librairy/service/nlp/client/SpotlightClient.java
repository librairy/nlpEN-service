package org.librairy.service.nlp.client;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.apache.commons.io.IOUtils;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class SpotlightClient {

    private static final Logger LOG = LoggerFactory.getLogger(SpotlightClient.class);

    private final CloseableHttpClient httpclient;


    public SpotlightClient() {
        this.httpclient = HttpClients.createDefault();
    }

    public Document request(String url, Map<String,String> parameters) throws Exception{
        Document doc = null;

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpPost.setHeader("Accept", "text/xml");

        List<NameValuePair> params = parameters.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        httpPost.setEntity(new UrlEncodedFormEntity(params));

        String xml = request(httpPost);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document document = builder.parse(is);
        return document;

    }

    public String request(HttpUriRequest method) throws Exception
    {

        String xml ="";
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute(method);

            // Execute the method.
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK)
            {
                LOG.error("Method failed: " + response.getStatusLine());
            }

            // Read the response body.
            // // Deal with the response.
            // // Use caution: ensure correct character encoding and is not binary data
            HttpEntity entity2 = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream responseBody = entity2.getContent();
            xml = IOUtils.toString(responseBody, "UTF-8");
            EntityUtils.consume(entity2);

        } catch (IOException e)
        {
            LOG.error("Fatal transport error: " + e.getMessage());
            throw new Exception("Transport error executing HTTP request.", e);
        }
        finally
        {
            // Release the connection.
            if (response != null) response.close();
        }
        return xml;

    }
}
