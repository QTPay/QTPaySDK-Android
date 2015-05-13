package com.example.qianfangdemo.base;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qfpay.sdk.utils.SharedPreferencesAccess;
import com.qfpay.sdk.utils.T;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

	public static SharedPreferencesAccess sharedPreferencesAccess;

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferencesAccess = SharedPreferencesAccess.getInstance(getApplicationContext());
		initConfigs();

	}

	public static String getAppCod() {
		return getCurrentUserConfig().getAppCode();
	}

	public static String getDomain() {
		return getEnvConfig().get(getIndex()[0]).getEnvUrl();
	}
	
	public static String getOutMerchant() {
		return getCurrentUserConfig().getOutMerchant();
	}


    /**
     * 获取当前环境配置坐标
     * @return
     */
	public static int[] getIndex() {
		String index = sharedPreferencesAccess.getString("CURRENT_IDEXT", "1,0");
		try {
			int env = Integer.parseInt(index.split(",")[0]);
			int user = Integer.parseInt(index.split(",")[1]);
			T.d("getCurrent current index:" + env + "," + user);
			return new int[] { env, user };
		} catch (Exception e) {
			e.printStackTrace();
			return new int[] { 0, 0 };
		}
	}

	public static UserConfig getCurrentUserConfig() {
		UserConfig userConfig = null;
		String index = sharedPreferencesAccess.getString("CURRENT_IDEXT", "1,0");
		try {
			int env = Integer.parseInt(index.split(",")[0]);
			int user = Integer.parseInt(index.split(",")[1]);
			userConfig = getEnvConfig().get(env).getUserConfigs().get(user);
		} catch (Exception e) {
			return getDefaultUserConfig();
		}
		return userConfig;
	}

	public static UserConfig getTestUserConfig() {

		UserConfig config = new UserConfig();
		config.setAppCode(ConstValue.appId_qa);
		config.setAppKey(ConstValue.apiKey);
		config.setOutMerchant("");
		return config;
	}

	public static UserConfig getDefaultUserConfig() {

		UserConfig config = new UserConfig();
		config.setAppCode(ConstValue.appId);
		config.setAppKey(ConstValue.apiKey);
		config.setOutMerchant("");
		return config;
	}

	public static UserConfig getSandboxConfig() {

		UserConfig config = new UserConfig();
		config.setAppCode(ConstValue.appId_sandbox);
		config.setAppKey(ConstValue.apiKey);
		config.setOutMerchant("");
		return config;
	}

    private static UserConfig getXianxiaConfig() {

        UserConfig config = new UserConfig();
        config.setAppCode(ConstValue.appId_dev);
        config.setAppKey(ConstValue.apiKey_dev);
        config.setOutMerchant("");
        return config;

    }

	public static List<EnvConfig> getEnvConfig() {
		String envconfig = sharedPreferencesAccess.getString("ENV_CONFIG", "");
		// T.i(envconfig);
		Gson gson = new Gson();
		List<EnvConfig> list = gson.fromJson(envconfig, new TypeToken<List<EnvConfig>>() {
		}.getType());
		return list;
	}

	public static void initConfigs() {
		if (TextUtils.isEmpty(sharedPreferencesAccess.getString("ENV_CONFIG", ""))) {
			Log.e("TAG", "================空");
			List<EnvConfig> list = new ArrayList<EnvConfig>();
			EnvConfig work = new EnvConfig();
			work.setEnvName("生产");
			work.setEnvUrl(ConstValue.domain);
			List<UserConfig> user = new ArrayList<UserConfig>();
			user.add(App.getDefaultUserConfig());
			work.setUserConfigs(user);
			list.add(work);

			EnvConfig sandbox = new EnvConfig();
			sandbox.setEnvName("沙盒");
			sandbox.setEnvUrl(ConstValue.domain_sandbox);
			List<UserConfig> user1 = new ArrayList<UserConfig>();
			user1.add(App.getSandboxConfig());
			sandbox.setUserConfigs(user1);
			list.add(sandbox);

			EnvConfig test = new EnvConfig();
			test.setEnvName("测试");
			test.setEnvUrl(ConstValue.domain_qa);
			List<UserConfig> user2 = new ArrayList<UserConfig>();
			user2.add(App.getTestUserConfig());
			test.setUserConfigs(user2);
			list.add(test);

            EnvConfig xianxia = new EnvConfig();
            xianxia.setEnvName("开发");
            List<UserConfig> user3 = new ArrayList<UserConfig>();
            user3.add(App.getXianxiaConfig());
            xianxia.setUserConfigs(user3);
            list.add(xianxia);
            xianxia.setEnvUrl(ConstValue.domain_xianxia);



			Gson gson = new Gson();
			String json = gson.toJson(list, new TypeToken<List<EnvConfig>>() {
			}.getType());
			sharedPreferencesAccess.putString("ENV_CONFIG", json);
			Log.e("TAG", "================init success");
		}
	}



    public static void replaceUserConfig(int parentIndex , int index, UserConfig config) {
		List<EnvConfig> envConfig = getEnvConfig();
		List<UserConfig> userconfig = getEnvConfig().get(parentIndex).getUserConfigs();
		userconfig.set(index,config);

		EnvConfig old = envConfig.get(parentIndex);
		old.setUserConfigs(userconfig);

		envConfig.set(parentIndex, old);

		T.d("add user config ：" + config.toString());
		Gson gson = new Gson();
		String json = gson.toJson(envConfig, new TypeToken<List<EnvConfig>>() {
		}.getType());
		T.i("update后的config： "+ json);
		sharedPreferencesAccess.putString("ENV_CONFIG", json);
	}
	
	public static void addUserConfig(int parentIndex, UserConfig config) {

		
		List<EnvConfig> envConfig = getEnvConfig();
		List<UserConfig> userconfig = getEnvConfig().get(parentIndex).getUserConfigs();
		userconfig.add(config);

		EnvConfig old = envConfig.get(parentIndex);
		old.setUserConfigs(userconfig);

		envConfig.set(parentIndex, old);

		T.d("add user config ：" + config.toString());
		Gson gson = new Gson();
		String json = gson.toJson(envConfig, new TypeToken<List<EnvConfig>>() {
		}.getType());
		T.i("添加后的config： "+ json);
		sharedPreferencesAccess.putString("ENV_CONFIG", json);

	}

	public static void updateSelection(String index) {
		sharedPreferencesAccess.putString("CURRENT_IDEXT", index);
	}

	public static void removeUserConfig(int parentindex, int childindex) {
		try {

			List<EnvConfig> envConfig = getEnvConfig();
			List<UserConfig> userconfig = getEnvConfig().get(parentindex).getUserConfigs();
			userconfig.remove(childindex);

			EnvConfig old = envConfig.get(parentindex);
			old.setUserConfigs(userconfig);

			envConfig.set(parentindex, old);

			Gson gson = new Gson();
			String json = gson.toJson(envConfig, new TypeToken<List<EnvConfig>>() {
			}.getType());
			sharedPreferencesAccess.putString("ENV_CONFIG", json);
			T.i("删除后的config： "+ json);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	
}