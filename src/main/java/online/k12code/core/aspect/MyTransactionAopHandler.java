package online.k12code.core.aspect;

import online.k12code.core.annotation.MyTransaction;
import online.k12code.core.datasource.DataSourceConnectHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author Carl
 * @description: 定义注解切面
 * @since 1.0.0
 */
@Aspect
@Component
public class MyTransactionAopHandler {

    private final DataSourceConnectHolder dataSourceConnectHolder;

    Class<? extends Throwable>[] ex;

    public MyTransactionAopHandler(DataSourceConnectHolder dataSourceConnectHolder) {
        this.dataSourceConnectHolder = dataSourceConnectHolder;
    }


    @Around("@annotation(myTransaction)")
    public Object TransactionProceed(ProceedingJoinPoint proceedingJoinPoint, MyTransaction myTransaction) throws Throwable {
        Object result = null;
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method == null) {
            return result;
        }
        MyTransaction annotation = method.getAnnotation(MyTransaction.class);
        if (annotation != null) {
            ex = myTransaction.rollbackFor();
        }
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            transactionAfterThrowing(e);
            throw e;
        }

        doCommit();
        return result;
    }

    /**
     * 检查异常，是目标异常或子类异常，就进行回滚，否则就提交
     */
    private void transactionAfterThrowing(Throwable throwable) {
        if (ex != null) {
            for (Class<? extends Throwable> e : ex) {
                    if (e.isAssignableFrom(throwable.getClass())) {
                        doRollBak();
                    }
            }
        }
        doCommit();
    }

    /**
     * 提交，关闭连接和清理线程
     */
    private void doCommit() {
        try {
            dataSourceConnectHolder.getConnection().commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConnectHolder.cleanHolder();
        }
    }

    /**
     * 回滚，关闭连接和清理线程
     */
    private void doRollBak() {
        try {
            dataSourceConnectHolder.getConnection().rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConnectHolder.cleanHolder();
        }
    }

}
