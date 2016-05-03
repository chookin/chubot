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
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        resultItems.getAllFields()
                .entrySet()
                .forEach(entry -> System.out.println(entry.getKey() + ":\t" + entry.getValue()));
        resultItems.getItems().stream().forEach(System.out::println);
    }
}
