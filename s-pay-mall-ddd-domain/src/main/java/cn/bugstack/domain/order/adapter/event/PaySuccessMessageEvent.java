package cn.bugstack.domain.order.adapter.event;

import cn.bugstack.types.event.BaseEvent;
import com.google.common.eventbus.EventBus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PaySuccessMessageEvent extends BaseEvent<PaySuccessMessageEvent.PaySuccessMessage> {


    @Override
    public EventMessage<PaySuccessMessage> buildEventMessage(PaySuccessMessage data) {
        return EventMessage.<PaySuccessMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return "Pay success";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaySuccessMessage{
        private String userId;
        private String tradeNo;
    }
}