package com.crm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EntityMerge {

	private static class EntityData {

		public EntityData(String methodCalled, Object returnData, Class<?> returnType) {

			this.methodCalled = methodCalled;
			this.returnData = returnData;
			this.returnType = returnType;

		}

		public String methodCalled;
		public Object returnData;
		public Class<?> returnType;

	}

	public static <T> T merge(T original, T updated) {
		if (!original.getClass().equals(updated.getClass())) {
			throw new RuntimeException("Classes must be the same");
		}

		try {
			Method[] updatedClassMethods = updated.getClass().getMethods();
			List<EntityData> entityDataList = new ArrayList<>();

			System.out.println("[EntityMerge] Starting getXXX process");

			for (int i = 0; i < updatedClassMethods.length; i++) {
				Method currentMethod = updatedClassMethods[i];
				String currentMethodName = currentMethod.getName();

				if (currentMethodName.startsWith("get")
				&& !currentMethodName.equals("getClass")
				&& currentMethod.getParameterCount() == 0
				&& currentMethod.getReturnType() != void.class) {
					System.out.println("[EntityMerge] Found a new getter '" + currentMethodName + "'");

					String methodCalled = currentMethodName.substring("get".length());
					Object returnData = currentMethod.invoke(updated);
					Class<?> returnType = currentMethod.getReturnType();
					
					if (returnData != null && !(returnData instanceof String && ((String) returnData).isEmpty())) {
						entityDataList.add(new EntityData(methodCalled, returnData, returnType));
					}
					
				}
			}

			System.out.println("[EntityMerge] Starting setXXX process");

			Method[] originalClassMethods = original.getClass().getMethods();

			for (int i = 0; i < originalClassMethods.length; i++) {
				Method currentMethod = originalClassMethods[i];
				String currentMethodName = currentMethod.getName();

				boolean found = false;

				for (int k = 0; k < entityDataList.size(); k++) {
					EntityData data = entityDataList.get(k);

					if (currentMethodName.equals("set" + data.methodCalled)) {
						found = true;
						System.out.println("[EntityMerge] Found setXXX method for '" + data.methodCalled + "'");			
				
						currentMethod.invoke(original, data.returnData);

						break;
					}
				}

				if (!found) {
					System.out.println("[EntityMerge] Could not find setXXX method for '" + currentMethodName + "'");
				}
			}

			return original;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
