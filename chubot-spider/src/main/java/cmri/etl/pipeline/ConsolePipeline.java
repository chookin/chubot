package cmri.etl.pipeline;

import cmri.etl.common.ResultItems;

/**
 * Created by zhuyin on 3/3/15.
 */
public class ConsolePipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        StringBuilder strb = new StringBuilder();
        strb.append("get page: ").append(resultItems.getRequest()).append("\n")
                .append("\t").append("request fields: ").append(resultItems.getAllFields()).append("\n")
                .append("\t").append("parsed items: ").append(resultItems.getItems()).append("\n")
                .append("\t").append("generated new requests:").append(resultItems.getTargetRequests());
        System.out.println(strb.toString());
    }
}
