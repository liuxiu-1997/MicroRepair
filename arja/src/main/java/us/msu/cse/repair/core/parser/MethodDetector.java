package us.msu.cse.repair.core.parser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import us.msu.cse.repair.core.util.Helper;

public class MethodDetector {
	List<ModificationPoint> modificationPoints;

	Map<String, ITypeBinding> declaredClasses;

	Set<String> dependences;
	URLClassLoader classLoader;

	Map<String, Map<String, MethodInfo>> declaredMethodMap;
	Map<String, Map<String, MethodInfo>> inheritedMethodMap;
	Map<String, Map<String, MethodInfo>> outerMethodMap;

	public MethodDetector(List<ModificationPoint> modificationPoints, Map<String, ITypeBinding> declaredClasses,
			Set<String> dependences) throws MalformedURLException {
		this.modificationPoints = modificationPoints;
		this.declaredClasses = declaredClasses;
		this.dependences = dependences;

		declaredMethodMap = new HashMap<String, Map<String, MethodInfo>>();
		inheritedMethodMap = new HashMap<String, Map<String, MethodInfo>>();
		outerMethodMap = new HashMap<String, Map<String, MethodInfo>>();

		if (dependences != null) {
			URL[] urls = Helper.getURLs(dependences);
			classLoader = new URLClassLoader(urls);
		} else
			classLoader = new URLClassLoader(new URL[0]);

	}
	//ClassLoader的具体作用就是将class文件加载到jvm虚拟机中，程序就可以正常运行了。但是，jvm启动的时候并不会一次性加载所有的class文件，而是根据需要动态的加载
	public void detect() throws ClassNotFoundException, IOException {
		for (ModificationPoint mp : modificationPoints)
			detectVisibleMethods(mp);
		classLoader.close();
	}

	private void detectVisibleMethods(ModificationPoint mp) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		String className = mp.getLCNode().getClassName();

		detectVisibleMethods(className);

		Map<String, MethodInfo> declaredMethods = declaredMethodMap.get(className);
		Map<String, MethodInfo> inheritedMethods = inheritedMethodMap.get(className);
		Map<String, MethodInfo> outerMethods = outerMethodMap.get(className);

		if (mp.isInStaticMethod()) {
			declaredMethods = getStaticMethods(declaredMethods);
			inheritedMethods = getStaticMethods(inheritedMethods);
		}

		mp.setDeclaredMethods(declaredMethods);
		mp.setInheritedMethods(inheritedMethods);
		mp.setOuterMethods(outerMethods);
	}

	void detectVisibleMethods(String className) throws ClassNotFoundException {
		if (declaredMethodMap.containsKey(className)) //declaredMethodMap是我在检测方法时做的标记：目的是为了让已经加载过的方法不再重复加载,当我还需要这么类的时候我
			return;                                   //可以直接在declaredMethodMap中加载.

		Map<String, MethodInfo> methods;
		String superClassName = null, outerClassName = null;
		boolean isStaticClass;

		if (declaredClasses.containsKey(className)) {    //我之前声明的declaredClasses中有这个类的话就在里面进行加载,如果没有的话我就要用classloader去加载
			//获得与指定类名关联的句柄，此句柄包含了该类的方法、变量
			ITypeBinding tb = declaredClasses.get(className);
			IMethodBinding[] mbs = tb.getDeclaredMethods();

			methods = Helper.getMethodInfos(mbs);              //方法为Map<String,MethodInfo>即Map<方法名,方法信息>方法信息包括：第几个修改点旗下的;返回值类型;返回值类型的名字;参数类型;参数名字;
			ITypeBinding superClass = tb.getSuperclass();
			if (superClass != null && !superClass.isInterface())
				superClassName = superClass.getBinaryName();

			ITypeBinding outerClass = tb.getDeclaringClass();
			if (outerClass != null && !outerClass.isInterface())
				outerClassName = outerClass.getBinaryName();

			isStaticClass = Modifier.isStatic(tb.getModifiers());
		} else {//只要是类加载了,就相当于这个类在运行了,我就可以直接使用这个类了.
			Class<?> target = classLoader.loadClass(className);
			Method[] mds = target.getDeclaredMethods();
			methods = Helper.getMethodInfos(mds);

			Class<?> superClass = target.getSuperclass();
			if (superClass != null && !superClass.isInterface())
				superClassName = superClass.getName();

			Class<?> outerClass = target.getDeclaringClass();
			if (outerClass != null && !outerClass.isInterface())
				outerClassName = outerClass.getName();

			isStaticClass = Modifier.isStatic(target.getModifiers());
		}
		declaredMethodMap.put(className, methods);

		Map<String, MethodInfo> inheritedMethods = new HashMap<String, MethodInfo>();
		if (superClassName != null) {
			detectVisibleMethods(superClassName);

			Map<String, MethodInfo> declaredMethodsOfSuper = declaredMethodMap.get(superClassName);
			Map<String, MethodInfo> inheritedMethodsOfSuper = inheritedMethodMap.get(superClassName);
            //继承的方法只有为  “public”  或  “protected”  或  “同一个包下的private方法时才有效”
			collectInheritedMethods(inheritedMethodsOfSuper, inheritedMethods, className, superClassName);
			collectInheritedMethods(declaredMethodsOfSuper, inheritedMethods, className, superClassName);
		}

		HashMap<String, MethodInfo> outerMethods = new HashMap<String, MethodInfo>();
		if (outerClassName != null) {
			detectVisibleMethods(outerClassName);

			Map<String, MethodInfo> declaredMethodsOfOuter = declaredMethodMap.get(outerClassName);
			Map<String, MethodInfo> inheritedMethodsOfOuter = inheritedMethodMap.get(outerClassName);
			Map<String, MethodInfo> outerMethodsOfOuter = outerMethodMap.get(outerClassName);

			collectOuterMethods(outerMethodsOfOuter, outerMethods, isStaticClass);
			collectOuterMethods(inheritedMethodsOfOuter, outerMethods, isStaticClass);
			collectOuterMethods(declaredMethodsOfOuter, outerMethods, isStaticClass);

			filterOuterMethods(outerMethods, methods, inheritedMethods);

		}

		inheritedMethodMap.put(className, inheritedMethods);
		outerMethodMap.put(className, outerMethods);
	}

	void filterOuterMethods(Map<String, MethodInfo> outerMethods, Map<String, MethodInfo> methods,
			Map<String, MethodInfo> inheritedMethods) {
		Iterator<String> iterator = outerMethods.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (containsMethodName(key, methods) || containsMethodName(key, inheritedMethods))
				iterator.remove();
		}
	}

	boolean containsMethodName(String key, Map<String, MethodInfo> methods) {
		String methodName = Helper.getMethodName(key);

		for (String str : methods.keySet()) {
			String name = Helper.getMethodName(str);
			if (name.equals(methodName))
				return true;
		}
		return false;
	}

	void collectInheritedMethods(Map<String, MethodInfo> map, Map<String, MethodInfo> inheritedMethods,
			String className, String superClassName) {
		for (Map.Entry<String, MethodInfo> entry : map.entrySet()) {
			boolean flag1 = Helper.isPublicMethod(entry.getValue());
			boolean flag2 = Helper.isProtectedMethod(entry.getValue());
			boolean flag3 = Helper.isPackagePrivateMethod(entry.getValue())
					&& Helper.isInSamePackage(className, superClassName);

			if (flag1 || flag2 || flag3)
				inheritedMethods.put(entry.getKey(), entry.getValue());
		}
	}

	void collectOuterMethods(Map<String, MethodInfo> map, Map<String, MethodInfo> outerMethods, boolean isStaticClass) {

		if (!isStaticClass) {
			for (Map.Entry<String, MethodInfo> entry : map.entrySet())
				outerMethods.put(entry.getKey(), entry.getValue());
		} else {
			for (Map.Entry<String, MethodInfo> entry : map.entrySet()) {
				if (Helper.isStaticMethod(entry.getValue()))
					outerMethods.put(entry.getKey(), entry.getValue());
			}
		}
	}

	Map<String, MethodInfo> getStaticMethods(Map<String, MethodInfo> methods) {
		Map<String, MethodInfo> staticMethods = new HashMap<String, MethodInfo>();

		for (Map.Entry<String, MethodInfo> entry : methods.entrySet()) {
			if (Helper.isStaticMethod(entry.getValue()))
				staticMethods.put(entry.getKey(), entry.getValue());
		}

		return staticMethods;
	}

}
