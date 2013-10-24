package sp.util.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Allows remote Solr administration
 *
 * TODO: security
 *
 * @author Paul Kulitski
 */
@Component
public class SolrAdministrator {

    RestTemplate restTemplate;
    @Value("${solr.host}")
    private String solrHost;
    protected static final Logger logger = LoggerFactory.getLogger(SolrAdministrator.class);

    public SolrAdministrator() {
        restTemplate = new RestTemplate();
    }

    public SolrAdministrator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final String FULL_DATA_IMPORT_COMMAND = "command=full-import";
    private static final String FULL_DATA_IMPORT_SUBPATH = "/dataimport";
    private static final int ATTEMPTS = 3;

    public String launchFullDataImport() {
        String dataimportUrl = solrHost + FULL_DATA_IMPORT_SUBPATH + "?"
                + FULL_DATA_IMPORT_COMMAND;
        ResponseEntity<String> res = null;
        for (int i = 0; i < ATTEMPTS; i++) {
            try {
                res = restTemplate.exchange(dataimportUrl,
                        HttpMethod.GET, HttpEntity.EMPTY, String.class);
                break;
            } catch (Exception ex) {
                if (i >= ATTEMPTS - 1) {
                    logger.error("Wasn't able to connect to Solr host to perform data-import."
                            + "Part of the application that uses Solr search will be disabled");
                }
            }
        }
        /* TODO: parse the response
         * ClientHttpRequest req = new SimpleClientHttpRequestFactory().createRequest(dataimportUrl, HttpMethod.GET);
         * req.execute();
         */
        if (res != null) {
            return res.getBody();
        } else {
            return null;
        }
    }
}
