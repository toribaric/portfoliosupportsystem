package org.nnga.tsp.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Map;

public class JsonView implements View {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_JSON_CONTENT_TYPE = "application/json";

    @Override
    public String getContentType() {
        return DEFAULT_JSON_CONTENT_TYPE;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding(getDefaultEncoding());
        response.setContentType(getContentType());

        Writer writer  = response.getWriter();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, model);
    }

    private String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }
}
