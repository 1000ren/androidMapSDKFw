package com.ctfo.mviewer.activity;

import java.util.ArrayList;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.ctfo.mviewer.R;

/**
 * 
 * @author fangwei
 * 
 * 定义图片按钮和菜单按钮 添加菜单
 * 
 */
public class Command {
	// fields
	public int cmdID = CMD_ID_NONE;
	public int cmdIcon = CMD_ICON_NONE;
	public String cmdName = "";

	// commands list
	private static ArrayList<Command> commands = new ArrayList<Command>();

	// commands
	public final static int CMD_ID_NONE = 0;
	public final static int CMD_ICON_NONE = 0;

	public final static int MENU_HOME = 1;
	public final static int MENU_ABOUT = 3;
	public final static int MENU_MORE = 5;
	public final static int MENU_EXIT = 9;
	
	public final static int MENU_FAVOR = 10;
	public final static int MENU_VIEWMAP = 11;
	public final static int MENU_APPUPDATE = 15;

	// 回原位
	public final static int MENU_BACKPOS = 20;
	// 跳转
	public final static int MENU_JUMP = 21;
	// 旋转
	public final static int MENU_ROTATE = 25;
	// 透视
	public final static int MENU_PERSPECTIVE = 26;
	// 清屏
	public final static int MENU_CLEARSRC = 30;
	// 清除缓存
	public final static int MENU_CLEARCACHE = 31;

	// 查询站点
	public final static int MENU_SEARCHSTATION = 35;
	// 查询线路
	public final static int MENU_SEARCHLINE = 36;
	// 查询POI
	public final static int MENU_SEARCHPOI = 38;
	// 设置指南针位置
	public final static int MENU_COMPASSSIZE = 39;


	// methods
	private Command(Context context, int cmdID, int idResName, int cmdIcon) {
		this.cmdID = cmdID;
		this.cmdName = context.getString(idResName);
		this.cmdIcon = cmdIcon;
	}

	public static void init(Context context)
	{
        //检查更新
        commands.add(new Command(context, Command.MENU_APPUPDATE, R.string.menu_appupdate, R.drawable.menu_about ));
        //关于我们
        commands.add(new Command(context, Command.MENU_ABOUT, R.string.menu_about, R.drawable.menu_about ));
        //退出
        commands.add(new Command(context, Command.MENU_EXIT, R.string.menu_exit, R.drawable.menu_about ));

        // 回原位
        commands.add(new Command(context, Command.MENU_BACKPOS, R.string.menu_backpos, CMD_ICON_NONE ) );
        // 跳转
        commands.add(new Command(context, Command.MENU_JUMP, R.string.menu_jump, CMD_ICON_NONE ) );
        // 旋转
        commands.add(new Command(context, Command.MENU_ROTATE, R.string.menu_rotate, CMD_ICON_NONE ) );
        // 透视
        commands.add(new Command(context, Command.MENU_PERSPECTIVE, R.string.menu_perspective, CMD_ICON_NONE ) );
        // 清屏
        commands.add(new Command(context, Command.MENU_CLEARSRC, R.string.menu_clearsrc, CMD_ICON_NONE ) );
        // 清除缓存
        commands.add(new Command(context, Command.MENU_CLEARCACHE, R.string.menu_clearcache, CMD_ICON_NONE ) );
        // 查询站点
        commands.add(new Command(context, Command.MENU_SEARCHSTATION, R.string.menu_searchstation, CMD_ICON_NONE ) );
        // 查询线路
        commands.add(new Command(context, Command.MENU_SEARCHLINE, R.string.menu_searchline, CMD_ICON_NONE ) );
        // 查询POI
        commands.add(new Command(context, Command.MENU_SEARCHPOI, R.string.menu_searchpoi, CMD_ICON_NONE ) );
        // 设置指南针位置
        commands.add(new Command(context, Command.MENU_COMPASSSIZE, R.string.menu_compasszie, CMD_ICON_NONE ) );
        
	}

	public static Command get(int cmd_id) {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (command != null)
				if (command.cmdID == cmd_id)
					return command;
		}
		return null;
	}

	/**
	 * 获取名称
	 * 
	 * @param cmd_id
	 * @return
	 */
	public static String getName(int cmd_id) {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (command != null)
				if (command.cmdID == cmd_id)
					return command.cmdName;
		}
		return "";
	}

	/**
	 * 获取图片
	 * 
	 * @param cmd_id
	 * @return
	 */
	public static int getIcon(int cmd_id) {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (command != null)
				if (command.cmdID == cmd_id)
					return command.cmdIcon;
		}
		return CMD_ICON_NONE;
	}

	/**
	 * 添加菜单
	 * 
	 * @param menu
	 * @param cm_id
	 * @return
	 */
	public static MenuItem addMenuItem(Menu menu, int cm_id) {
		MenuItem item = menu.add(0, cm_id, Menu.NONE, Command.getName(cm_id));
		int cmd_icon = Command.getIcon(cm_id);
		if (item != null && cmd_icon != CMD_ICON_NONE)
			item.setIcon(cmd_icon);
		return item;

	}

	/**
	 * 添加子菜单
	 * 
	 * @param menu
	 * @param cm_id
	 * @param icon_res_id
	 * @return
	 */
	public static SubMenu addSubMenuItem(Menu menu, int cm_id, int icon_res_id) {
		SubMenu item = menu.addSubMenu(0, cm_id, Menu.NONE, Command
				.getName(cm_id));
		if (item != null)
			item.setIcon(icon_res_id);
		return item;
	}

}
