package cc.openxiot.common.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import java.util.Map;

@PreMatching
@Provider
public class LoggerFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    Logger logger;

    @Inject
    ObjectMapper mapper;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String method = ctx.getMethod();
        String path = ctx.getUriInfo().getPath();

        logger.infov("{0} {1}", method, path);

//        // 1. 获取并打印查询参数
//        MultivaluedMap<String, String> queryParams = ctx.getUriInfo().getQueryParameters();
//        String queryParamsString = formatQueryParams(queryParams);
//
//        // 2. 获取并打印请求头
//        MultivaluedMap<String, String> headers = ctx.getHeaders();
//        String headersString = formatHeaders(headers);
//
//        // 打印日志
//        logger.infov("{0} {1}\nQuery Params: {2}\nHeaders: {3}", method, path, queryParamsString, headersString);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)  {
        String method = request.getMethod();
        String path = request.getUriInfo().getPath();
        int status = response.getStatus();
        String reason = response.getStatusInfo().getReasonPhrase();
        Object entity = response.getEntity();
        String body = "";
        if (entity != null) {
            if (entity instanceof String) {
                body = entity.toString();
            } else {
                String type = response.getHeaderString(HttpHeaders.CONTENT_TYPE);
                if (type != null) {
                    if (type.equals(MediaType.APPLICATION_JSON)) {
                        try {
                            body = mapper.writeValueAsString(entity);
                        } catch (JsonProcessingException e) {
                            logger.error(e);
                        }
                    }
                }
            }
        }

        logger.infov("{0} {1} => {2} {3} {4}", method, path, status, reason, body);
    }

    /**
     * 格式化查询参数为易读的字符串
     */
    private String formatQueryParams(MultivaluedMap<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, java.util.List<String>> entry : queryParams.entrySet()) {
            sb.append(entry.getKey()).append("=").append(String.join(",", entry.getValue())).append(", ");
        }

        // 去掉最后的逗号和空格
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * 格式化请求头为易读的字符串
     * 注意：考虑安全，建议对敏感头信息（如Authorization）进行掩码处理
     */
    private String formatHeaders(MultivaluedMap<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, java.util.List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            // 对敏感头信息进行掩码处理
            if ("Authorization".equalsIgnoreCase(key) || "Cookie".equalsIgnoreCase(key)) {
                sb.append(key).append("=").append("******, ");
            } else {
                sb.append(key).append("=").append(String.join(",", entry.getValue())).append(", ");
            }
        }

        // 去掉最后的逗号和空格
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");

        return sb.toString();
    }
}
