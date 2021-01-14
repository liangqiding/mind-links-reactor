package exception;

import com.mind.links.common.enums.LinksExceptionEnum;
import com.mind.links.common.exception.LinksException;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.util.function.Consumer;

/**
 * date: 2021-01-14 10:04
 * description tcp 公共异常处理
 *
 * @author qiDing
 */
@Slf4j
public class LinksExceptionTcp {

    public static <T> Mono<T> errors(String msg) {
        return errors(LinksExceptionEnum.LINKS_ERROR.getCode(), msg);
    }

    public static <T> Mono<T> errors(Integer code, String msg) {
        return Mono.error(new LinksException(code, msg));
    }


    public static <T> Mono<T> errors(Throwable cause) {
        return Mono.error(cause);
    }

    public static <T> Mono<T> errors(Throwable cause,Channel channel, Consumer<Channel> consumer) {
        consumer.accept(channel);
        return errors(cause);
    }



}
