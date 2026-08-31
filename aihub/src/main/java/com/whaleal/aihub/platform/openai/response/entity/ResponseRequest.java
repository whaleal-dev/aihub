package com.whaleal.aihub.platform.openai.response.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.whaleal.aihub.listener.StreamExecutionOptions;
import com.whaleal.aihub.platform.openai.chat.entity.StreamOptions;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseRequest {

    @NonNull
    private String model;


    private Object input;


    private List<String> include;


    private String instructions;

    @JsonProperty("previous_response_id")
    private String previousResponseId;

    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    private Map<String, Object> metadata;

    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;


    private Object reasoning;

    private Boolean store;

    private Boolean stream;

    @JsonProperty("stream_options")
    private StreamOptions streamOptions;

    private Double temperature;


    private Object text;

    @JsonProperty("tool_choice")
    private Object toolChoice;

    private List<Object> tools;

    @JsonIgnore
    private List<String> functions;

    @JsonProperty("top_p")
    private Double topP;

    private String truncation;

    private String user;


    @JsonIgnore
    @Singular("extraBody")
    private Map<String, Object> extraBody;

    @JsonIgnore
    private StreamExecutionOptions streamExecution;

    @JsonAnyGetter
    public Map<String, Object> getExtraBody() {
        return extraBody;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    public static class ResponseRequestBuilder {
        private List<String> functions;

        public ResponseRequestBuilder functions(String... functions) {
            if (this.functions == null) {
                this.functions = new ArrayList<String>();
            }
            this.functions.addAll(Arrays.asList(functions));
            return this;
        }

        public ResponseRequestBuilder functions(List<String> functions) {
            if (this.functions == null) {
                this.functions = new ArrayList<String>();
            }
            if (functions != null) {
                this.functions.addAll(functions);
            }
            return this;
        }
    }
}

