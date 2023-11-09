package server;

import real.Ninja;
import real.ClanManager;
import real.PlayerManager;
import java.io.IOException;
import patch.EventItem;
import real.Item;
import real.ItemData;
import threading.Message;
import real.User;
import threading.Server;
import threading.Manager;
import static server.MenuController.lamSuKien;
import static server.util.CheckString;

public class Draw {

    private static final Server server;
    static LogHistory LogHistory = new LogHistory();

    public static void Draw(final User p, final Message m) throws IOException {
        final short menuId = m.reader().readShort();
        final String str = m.reader().readUTF();
        m.cleanup();
        util.Debug("menuId " + menuId + " str " + str);
        byte b = -1;
        try {
            b = m.reader().readByte();
        } catch (IOException ex) {
        }
        m.cleanup();
        switch (menuId) {
            case 1: {
                if (p.nj.quantityItemyTotal(279) <= 0) {
                    break;
                }
                final Ninja c = PlayerManager.getInstance().getNinja(str);
                if (c.getPlace() != null && !c.getPlace().map.isLangCo() && c.getPlace().map.getXHD() == -1 && !c.getPlace().map.isLangTruyenThuyet()) {
                    p.nj.getPlace().leave(p);
                    p.nj.get().x = c.get().x;
                    p.nj.get().y = c.get().y;
                    c.getPlace().Enter(p);
                    return;
                }
                p.sendYellowMessage("Ví trí người này không thể đi tới");
                break;
            }
            case 41_0:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja n = PlayerManager.getInstance().getNinja(str);
                if (n != null) {
                    server.menu.sendWrite(p, (short) 41_0_0, "ID vật phẩm :");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 41_0_0:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.idItemGF = str;
                if (p.idItemGF != null) {
                    server.menu.sendWrite(p, (short) 41_0_1, "Nhập số lượng :");
                } else {
                    p.sendYellowMessage("Nhập sai");
                }
                break;
            case 41_0_1:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.itemQuantityGF = str;
                p.sendItem1();
                break;
            case 41_1:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja u = PlayerManager.getInstance().getNinja(str);
                if (u != null) {
                    server.menu.sendWrite(p, (short) 41_1_0, "ID vật phẩm :");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 41_1_0:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.idItemGF = str;
                if (p.idItemGF != null) {
                    server.menu.sendWrite(p, (short) 41_1_1, "Nhập số lượng :");
                } else {
                    p.sendYellowMessage("Nhập sai");
                }
                break;
            case 41_1_1:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.itemQuantityGF = str;
                if (p.idItemGF != null) {
                    server.menu.sendWrite(p, (short) 41_1_2, "Nhập cấp độ cho trang bị :");
                } else {
                    p.sendYellowMessage("Nhập sai");
                }
                break;
            case 41_1_2:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.itemUpgradeGF = str;
                if (p.idItemGF != null) {
                    server.menu.sendWrite(p, (short) 41_1_3, "Nhập hệ trang bị:");
                } else {
                    p.sendYellowMessage("Nhập sai");
                }
                break;
            case 41_1_3:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.itemSysGF = str;
                p.sendTB();
                break;
            //Làm bánh thập cẩm
            case 6: {
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                long soluong = Integer.parseInt(str);
                if (p.nj.quantityItemyTotal(292) >= 1 * soluong && p.nj.quantityItemyTotal(293) >= 1 * soluong && p.nj.quantityItemyTotal(294) >= 1 * soluong && p.nj.quantityItemyTotal(295) >= 1 * soluong && p.nj.quantityItemyTotal(297) >= 1 * soluong) {
                    if (soluong > 1000) {
                        p.session.sendMessageLog("Chỉ có thể làm được tối đa 1000 cái 1 lượt");
                        return;
                    }
                    if (p.nj.getAvailableBag() == 0) {
                        p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    } else {
                        p.nj.removeItemBags(292, (int) (1 * soluong));
                        p.nj.removeItemBags(293, (int) (1 * soluong));
                        p.nj.removeItemBags(294, (int) (1 * soluong));
                        p.nj.removeItemBags(295, (int) (1 * soluong));
                        p.nj.removeItemBags(297, (int) (1 * soluong));
                        Item it = ItemData.itemDefault(298);
                        it.quantity = (int) (1 * soluong);
                        p.nj.addItemBag(true, it);
                    }
                    return;
                } else {
                    p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                }
                break;
            }
            //Làm bánh dẻo
            case 7: {
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                long soluong = Integer.parseInt(str);
                if (p.nj.quantityItemyTotal(292) >= 1 * soluong && p.nj.quantityItemyTotal(294) >= 1 * soluong && p.nj.quantityItemyTotal(295) >= 1 * soluong && p.nj.quantityItemyTotal(297) >= 1 * soluong) {
                    if (soluong > 1000) {
                        p.session.sendMessageLog("Chỉ có thể làm được tối đa 1000 cái 1 lượt");
                        return;
                    }
                    if (p.nj.getAvailableBag() == 0) {
                        p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    } else {
                        p.nj.removeItemBags(292, (int) (1 * soluong));
                        p.nj.removeItemBags(294, (int) (1 * soluong));
                        p.nj.removeItemBags(295, (int) (1 * soluong));
                        p.nj.removeItemBags(297, (int) (1 * soluong));
                        Item it = ItemData.itemDefault(299);
                        it.quantity = (int) (1 * soluong);
                        p.nj.addItemBag(true, it);
                    }
                    return;
                } else {
                    p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                }
                break;
            }
            //Làm bánh đậu xanh
            case 8: {
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                long soluong = Integer.parseInt(str);
                if (p.nj.quantityItemyTotal(292) >= 1 * soluong && p.nj.quantityItemyTotal(293) >= 1 * soluong && p.nj.quantityItemyTotal(294) >= 1 * soluong && p.nj.quantityItemyTotal(296) >= 1 * soluong) {
                    if (soluong > 1000) {
                        p.session.sendMessageLog("Chỉ có thể làm được tối đa 1000 cái 1 lượt");
                        return;
                    }
                    if (p.nj.getAvailableBag() == 0) {
                        p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    } else {
                        p.nj.removeItemBags(292, (int) (1 * soluong));
                        p.nj.removeItemBags(293, (int) (1 * soluong));
                        p.nj.removeItemBags(294, (int) (1 * soluong));
                        p.nj.removeItemBags(296, (int) (1 * soluong));
                        Item it = ItemData.itemDefault(300);
                        it.quantity = (int) (1 * soluong);
                        p.nj.addItemBag(true, it);
                    }
                    return;
                } else {
                    p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                }
                break;
            }
            //Làm bánh pía
            case 9: {
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                long soluong = Integer.parseInt(str);
                if (p.nj.quantityItemyTotal(292) >= 1 * soluong && p.nj.quantityItemyTotal(293) >= 1 * soluong && p.nj.quantityItemyTotal(294) >= 1 * soluong && p.nj.quantityItemyTotal(296) >= 1 * soluong) {
                    if (soluong > 1000) {
                        p.session.sendMessageLog("Chỉ có thể làm được tối đa 1000 cái 1 lượt");
                        return;
                    }
                    if (p.nj.getAvailableBag() == 0) {
                        p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    } else {
                        p.nj.removeItemBags(292, (int) (1 * soluong));
                        p.nj.removeItemBags(293, (int) (1 * soluong));
                        p.nj.removeItemBags(294, (int) (1 * soluong));
                        p.nj.removeItemBags(296, (int) (1 * soluong));
                        Item it = ItemData.itemDefault(301);
                        it.quantity = (int) (1 * soluong);
                        p.nj.addItemBag(true, it);
                    }
                    return;
                } else {
                    p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                }
                break;
            }

            //đổi xu số lượng
            case 10: {
                if (!CheckString(str, "^[0-9]+$")) {
                    p.session.sendMessageLog("Số lượng nhập vào không hợp lệ");
                    return;
                }
                long soluong = Integer.parseInt(str);
                if (soluong > 100) {
                    p.session.sendMessageLog("Chỉ có thể đổi tối đa 100 lần");
                    return;
                }
                if (p.luong < 50 * soluong) {
                    p.session.sendMessageLog("Không đủ lượng để đổi");
                    return;
                } else {
                    p.nj.upxuMessage((50000000 * soluong));
                    p.upluongMessage(-(50L * soluong));
                    LogHistory.log4("Xu : " + p.nj.name + " da doi xu voi so luong " + soluong);
                    LogHistory.log8("Xu : " + p.nj.name + " da doi xu voi so luong " + soluong);
                    if (soluong > 100 || soluong < 0) {
                        LogHistory.log10("Xu : " + p.nj.name + " da doi xu voi so luong " + soluong);

                    }
                }
                break;
            }
            //đổi yên số lượng
            case 11: {
                if (!CheckString(str, "^[0-9]+$")) {
                    p.session.sendMessageLog("Số lượng nhập vào không hợp lệ");
                    return;
                }
                long soluong = Integer.parseInt(str);
                if (soluong > 100) {
                    p.session.sendMessageLog("Chỉ có thể đổi tối đa 100 lần");
                    return;
                }
                if (p.luong < 50 * soluong) {
                    p.session.sendMessageLog("Không đủ lượng để đổi");
                    return;
                } else {
                    p.nj.upyenMessage((50000000 * soluong));
                    p.upluongMessage(-(50L * soluong));
                    LogHistory.log4("Yen : " + p.nj.name + " da doi yen voi so luong " + soluong);
                    LogHistory.log8("Yen : " + p.nj.name + " da doi yen voi so luong " + soluong);
                }
                break;
            }
            case 49:
                if (str.contains("%")) {
                    p.session.sendMessageLog("Mã quà tặng không hợp lệ");
                    return;
                }
                p.giftcode = str;
                p.giftcode();
                break;
            case 50: {
                ClanManager.createClan(p, str);
                break;
            }
            case 51: {
                p.passnew = "";
                p.passold = str;
                p.changePassword();
                Draw.server.menu.sendWrite(p, (short) 52, "Nhập mật khẩu mới");
                break;
            }
            case 52: {
                p.passnew = str;
                p.changePassword();
                break;
            }
            case 55:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja a = PlayerManager.getInstance().getNinja(str);
                if (a != null) {
                    server.menu.sendWrite(p, (short) 56, "Nhập ID vật phẩm:");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 56:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.idItemGF = str;
                server.menu.sendWrite(p, (short) 57, "Nhập số lượng vật phẩm:");
                break;
            case 57:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.itemQuantityGF = str;
                p.sendItem();
                LogHistory.log("BUFF: " + p.nameUS + " da duoc admin buff item " + p.idItemGF + " so luong " + p.itemQuantityGF + " item");
                break;
            case 58:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja a1 = PlayerManager.getInstance().getNinja(str);
                if (a1 != null) {
                    server.menu.sendWrite(p, (short) 59, "Nhập lượng:");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 59:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.luongGF = str;
                p.sendLuong();
                LogHistory.log("BUFF: " + p.nameUS + " da duoc admin buff " + p.luongGF + " luong");
                break;
            case 60:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja a2 = PlayerManager.getInstance().getNinja(str);
                if (a2 != null) {
                    server.menu.sendWrite(p, (short) 61, "Nhập xu:");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 61:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.xuGF = str;
                p.sendXu();
                LogHistory.log("BUFF: " + p.nameUS + " da duoc admin buff " + p.xuGF + " xu");
                break;
            case 62:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja a3 = PlayerManager.getInstance().getNinja(str);
                if (a3 != null) {
                    server.menu.sendWrite(p, (short) 63, "Nhập yên:");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 63:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.yenGF = str;
                p.sendYen();
                LogHistory.log("BUFF: " + p.nameUS + " da duoc admin buff " + p.yenGF + " yen");
                break;
            case 64:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.nameUS = str;
                Ninja a4 = PlayerManager.getInstance().getNinja(str);
                if (a4 != null) {
                    server.menu.sendWrite(p, (short) 65, "Nhập lời nhắn:");
                } else {
                    p.sendYellowMessage("Tên người nhận sai hoặc không online");
                }
                break;
            case 65:
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                p.messGF = str;
                p.sendMess();
                break;
            case 66:
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                if (p.nj.getAvailableBag() == 0) {
                    p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    return;
                }
                EventItem entry = EventItem.entrys[0];
                if (str != null) {
                    int quantity = Integer.parseInt(str);
                    if (quantity <= 1000) {
                        for (int i = 0; i < quantity; i++) {
                            lamSuKien(p, entry);
                        }
                    } else {
                        p.sendYellowMessage("Bạn chỉ có thể làm 1000 cái 1 lượt");
                    }
                }
                break;
            case 67:
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                if (p.nj.getAvailableBag() == 0) {
                    p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    return;
                }
                EventItem entry1 = EventItem.entrys[1];
                if (str != null) {
                    int quantity = Integer.parseInt(str);
                    if (quantity <= 1000) {
                        for (int i = 0; i < quantity; i++) {
                            lamSuKien(p, entry1);
                        }
                    } else {
                        p.sendYellowMessage("Bạn chỉ có thể làm 1000 cái 1 lượt");
                    }
                }
                break;
            case 68:
                if (str.contains("-")) {
                    p.sendYellowMessage("Số lượng không hợp lệ");
                    return;
                }
                if (p.nj.getAvailableBag() == 0) {
                    p.sendYellowMessage("Hành trang không đủ chỗ trống");
                    return;
                }
                EventItem entry2 = EventItem.entrys[2];
                if (str != null) {
                    int quantity = Integer.parseInt(str);
                    if (quantity <= 1000) {
                        for (int i = 0; i < quantity; i++) {
                            lamSuKien(p, entry2);
                        }
                    } else {
                        p.sendYellowMessage("Bạn chỉ có thể làm 1000 cái 1 lượt");
                    }
                }
                break;
            case 100: {
//                if (b == 1) {
//                    p.session.sendMessageLog("Chức năng tạm bảo trì");
//                    return;
//                }
                final String num = str.replaceAll(" ", "").trim();
                if (num.length() > 10 || !util.checkNumInt(num) || b < 0 || b >= Draw.server.manager.rotationluck.length) {
                    return;
                }
                final int xujoin = Integer.parseInt(num);
                Draw.server.manager.rotationluck[b].joinLuck(p, xujoin);
                break;
            }
            case 101: {
                if (b < 0 || b >= Draw.server.manager.rotationluck.length) {
                    return;
                }
                if (b == 0 && p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 0 && p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2] || p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 1 && p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                    p.nj.taskDanhVong[1]++;
                }
                if (b == 1 && p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 1 && p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                    p.nj.taskDanhVong[1]++;
                }
                Draw.server.manager.rotationluck[b].luckMessage(p);
                break;
            }
            case 102: {
                p.typemenu = 92;
                MenuController.doMenuArray(p, new String[]{"Vòng xoay vip", "Vòng xoay thường"});
                break;
            }
            case 9991: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                Manager.chatKTG(str);
                break;
            }
            case 9992: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                if (!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.get().setSpoint(p.nj.getSpoint() + num);
                p.loadSkill();
                break;
            }
            case 9993: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                if (!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.get().updatePpoint(p.nj.get().getPpoint() + num);
                p.updatePotential();
                break;
            }
            case 9994: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                if (!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.setLevel(p.nj.getLevel() + num);
                break;
            }
            case 9995: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                if (!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.upyenMessage(num);
                break;
            }
            case 9996: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                if (!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.upluongMessage(num);
                break;
            }
            case 9997: {
                if (p.id != 1) {
                    p.session.sendMessageLog("Bạn không có quyền admin");
                    return;
                }
                if (!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.upxuMessage(num);
                break;
            }
            case 9998: {
                try {
                    if (p.id != 1) {
                        p.session.sendMessageLog("Bạn không có quyền admin");
                        return;
                    }
                    if (!util.checkNumInt(str) || str.equals("")) {
                        p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                        return;
                    }
                    String check = str.replaceAll(" ", "").trim();
                    int minues = Integer.parseInt(check);
                    if (minues < 0 || minues > 10) {
                        p.session.sendMessageLog("Giá trị nhập vào từ 0 -> 10 phút");
                        return;
                    }
                    p.sendYellowMessage("Đã kích hoạt bảo trì Server sau " + minues + " phút.");
                    for (int i = 0; i < minues; i++) {
                        Manager.serverChat("Thông báo", "Máy chủ sẽ tiến hành bảo trì sau " + (minues - i) + " phút nữa. Vui lòng thoát game để tránh mất dữ liệu.");
                        Thread.sleep(60000);
                    }
                    PlayerManager.getInstance().Clear();
                    server.stop();
                    break;
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    static {
        server = Server.getInstance();
    }
}
