package org.processmining.framework.plugin;

import java.util.List;
import java.util.concurrent.Executor;

import org.processmining.framework.connections.Connection;
import org.processmining.framework.plugin.events.Logger;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.plugin.events.PluginLifeCycleEventListener;
import org.processmining.framework.plugin.events.ProgressEventListener;
import org.processmining.framework.plugin.impl.FieldNotSetException;
import org.processmining.framework.plugin.impl.FieldSetException;
import org.processmining.framework.util.Pair;

public interface PluginContext extends GlobalContext, ObjectConstructor {

	/**
	 * Returns a new plugin context instance, which can be used to invoke other
	 * plugins.
	 * 返回一个新的插件上下文实例，可用于调用其他插件。
	 * @return the new plugin context instance
	 */
	PluginContext createChildContext(String label);

	/* === Getters ==================================================== */

	/**
	 * Returns the progress object corresponding to this context
	 * 返回与此上下文相对应的进度对象
	 * @return the progress object corresponding to this context
	 */
	Progress getProgress();

	/**
	 * Returns the list of registered progress listeners
	 * 返回已注册的进度侦听器的列表
	 * @return the list of registered progress listeners
	 */
	ProgressEventListener.ListenerList getProgressEventListeners();

	/**
	 * Returns the list of registered plugin life cycle listeners.
	 * 返回已注册插件生命周期侦听器的列表。
	 * @return the list of registered plugin life cycle listeners.
	 */
	PluginLifeCycleEventListener.List getPluginLifeCycleEventListeners();

	/**
	 * Each PluginContext should carry an ID. This ID is unique within this
	 * plugin context's global context.
	 * 每个PluginContext都应该带有一个ID。 这个ID在这个插件上下文的全局上下文中是唯一的。
	 * @return the ID of this context
	 */
	PluginContextID getID();

	/**
	 * Returns the label of this context.
	 * 返回此上下文的标签。
	 * @return
	 */
	String getLabel();

	/**
	 * Return the plugin descriptor and method index of the plugin which is
	 * invoked in this context. This descriptor is set by the
	 * PluginDescriptor.invoke() method and will not be set yet before
	 * PluginManager.invoke() is called.
	 * 返回插件的插件描述符和方法索引
     *在这种情况下被调用。 这个描述符是由
     * PluginDescriptor.invoke（）方法，并且不会在之前设置
     *调用PluginManager.invoke（）。
	 * @return the descriptor of the plugin which is invoked in this context If
	 *         the plugin is not set yet, a pair of (null,-1) is returned
	 */
	Pair<PluginDescriptor, Integer> getPluginDescriptor();

	/**
	 * Returns the context which created this context or null if it has no
	 * parent.
	 * 返回创建此上下文的上下文，如果没有父级，则返回null。
	 * @return
	 */
	PluginContext getParentContext();

	/**
	 * Returns a list of all child contexts which have been created with
	 * createChildContext().
	 * 返回已经创建的所有子上下文的列表
     * createChildContext（）。
	 * @return
	 */
	List<PluginContext> getChildContexts();

	/**
	 * This method returns the PluginExecutionResult of the plugin which is
	 * invoked in this context. This future result is set by
	 * PluginManager.invoke() and will not be available (will be null) until the
	 * invoke() method is called.
	 * 这个方法返回插件的PluginExecutionResult
     *在这种情况下被调用。 这个未来的结果是由
     * PluginManager.invoke（）将不可用（将为空），直到
     *调用invoke（）方法。
	 * @return The PluginExecutionResult that represents the result of this
	 *         plugin invocation
	 * @throws FieldNotSetException
	 *             If the future is not know to this context
	 */
	PluginExecutionResult getResult();

	/**
	 * This method should only be used by a plugin, in the body of that plugin.
	 * That is the only location, where it is guaranteed that each result object
	 * in getResults() can safely be cast to a ProMFuture.
	 * 这个方法只能被插件的主体使用。
*这是唯一的位置，它保证每个结果对象
*getResults（）中的*可以安全地转换为ProMFuture。
	 * @param i
	 * @return
	 */
	ProMFuture<?> getFutureResult(int i);

	/**
	 * Returns an executor which can be used to execute plugins in child
	 * contexts.
	 * 返回一个可用于执行子插件的执行器
*上下文。
	 * @return
	 */
	Executor getExecutor();

	/**
	 * Returns true if this is a distant child of context, i.e. true if
	 * getParent.getID().equals(context.getID()) ||
	 * getParent().isDistantChildOf(context);
	 * 如果这是一个遥远的上下文子项，则返回true，即，如果为true，则返回true
     * getParent.getID（）。equals（context.getID（））||
     * getParent（）。isDistantChildOf（context）;
	 * @param context
	 * @return
	 */
	boolean isDistantChildOf(PluginContext context);

	/*
	 * === Setters: should only be called by the framework!
	 * ===============================
	 */

	void setFuture(PluginExecutionResult resultToBe);

	void setPluginDescriptor(PluginDescriptor descriptor, int methodIndex) throws FieldSetException,
			RecursiveCallException;

	boolean hasPluginDescriptorInPath(PluginDescriptor descriptor, int methodIndex);

	/**
	 * The provided String is provided to the context for information. It can
	 * for example signal a state change of a plugin. Note that some contexts
	 * can completely ignore this message.
	 * 提供的字符串提供给上下文以供参考。 它可以
     *例如表示插件的状态变化。 请注意一些情况
     *可以完全忽略此消息。
	 * @param message
	 *            the message to log
	 * @param level
	 *            the message level
	 */
	void log(String message, MessageLevel level);

	/**
	 * Same as calling log(message, MessageLevel.NORMAL);
	 * 与呼叫日志（message，MessageLevel.NORMAL）相同;
	 * @param message
	 *            The message
	 */
	void log(String message);

	/**
	 * The provided Exception is provided to the context. It signals the context
	 * about an error in the plugin, that specifically lead to abnormal
	 * termination. The plugin signaling the exception is no longer executing!
	 * 提供的例外提供给上下文。 它表示上下文
*关于插件中的错误，明确导致异常
*终止。 指示异常的插件不再执行！
	 * @param exception
	 *            the exception thrown
	 */
	void log(Throwable exception);

	/**
	 * Returns the list of logging listeners registered to this context.
	 * 返回注册到此上下文的日志记录侦听器的列表。返回注册到此上下文的日志记录侦听器的列表。
	 * @return
	 */
	Logger.ListenerList getLoggingListeners();

	/**
	 * Returns the root plugin context. This is an instance of PluginContext of
	 * which all other contexts are distant childs.
	 * 返回根插件上下文。 这是一个PluginContext的实例
     *其他所有情况都是遥远的孩子。
	 * @return
	 */
	PluginContext getRootContext();

	/**
	 * Delete this child from this context.
	 * 
	 * @param child
	 * @returns true if this child was a child of the context and has now been
	 *          deleted. false otherwise
	 */
	boolean deleteChild(PluginContext child);

	/**
	 * Registers the given connection in the global context. The implementation
	 * is
	 * 
	 * addConnection(this,c);
	 * 
	 * @param c
	 */
	<T extends Connection> T addConnection(T c);

	void clear();
}
