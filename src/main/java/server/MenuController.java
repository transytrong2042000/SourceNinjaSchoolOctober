package server;

import boardGame.Place;
import lombok.SneakyThrows;
import lombok.val;
import patch.*;
import patch.clan.ClanTerritory;
import patch.clan.ClanTerritoryData;
import patch.interfaces.IBattle;
import patch.tournament.GeninTournament;
import patch.tournament.KageTournament;
import patch.tournament.Tournament;
import patch.tournament.TournamentData;
import real.*;
import tasks.TaskHandle;
import tasks.TaskList;
import tasks.Text;
import threading.Manager;
import threading.Map;
import threading.Message;
import threading.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static patch.Constants.TOC_TRUONG;
import static patch.ItemShinwaManager.*;
import static patch.TaskOrder.*;
import static patch.tournament.Tournament.*;
import static real.User.TypeTBLOption.*;
import static server.GameScr.optionden;
import static server.GameScr.paramden;

public class MenuController {

    public static final String MSG_HANH_TRANG = "Hành trang ko đủ chổ trống";

    public static final int MIN_YEN_NVHN = 20;
    public static final int MAX_YEN_NVHN = 25;

    Server server;
    protected ArrayList<Player> player10k = new ArrayList<>();
    protected ArrayList<Player> player20k = new ArrayList<>();
    protected ArrayList<Player> player50k = new ArrayList<>();
    protected String xuhu10k = "";
    protected String xuhu20k = "";
    protected String xuhu50k = "";
    protected int xuwin10k = 0;
    protected int xuwin20k = 0;
    protected int xuwin50k = 0;
    protected int xuclone10k = util.nextInt(0, 1000);
    protected int xuclone20k = util.nextInt(0, 1000);
    protected int xuclone50k = util.nextInt(0, 1000);

    public MenuController() {
        this.server = Server.getInstance();
    }

    public void sendMenu(final User p, final Message m) throws IOException {
        final byte npcId = m.reader().readByte();
        byte menuId = m.reader().readByte();
        final byte optionId = m.reader().readByte();

        val ninja = p.nj;

        if (TaskHandle.isTaskNPC(ninja, npcId) && Map.isNPCNear(ninja, npcId)) {
            // TODO SELECT MENU TASK
            menuId = (byte) (menuId - 1);
            if (ninja.getTaskIndex() == -1) {

                if (menuId == -1) {
                    TaskHandle.Task(ninja, (short) npcId);
                    return;
                }
            } else if (TaskHandle.isFinishTask(ninja)) {
                if (menuId == -1) {
                    TaskHandle.finishTask(ninja, (short) npcId);
                    return;
                }
            } else if (ninja.getTaskId() == 1) {
                if (menuId == -1) {
                    TaskHandle.doTask(ninja, (short) npcId, menuId, optionId);
                    return;
                }
            } else if (ninja.getTaskId() == 7) {
                if (menuId == -1) {
                    TaskHandle.doTask(ninja, (short) npcId, menuId, optionId);
                    return;
                }
            } else if (ninja.getTaskId() == 8 || ninja.getTaskId() == 0) {
                boolean npcTalking = TaskHandle.npcTalk(ninja, menuId, npcId);
                if (npcTalking) {
                    return;
                }

            } else if (ninja.getTaskId() == 13) {
                if (menuId == -1) {
                    if (ninja.getTaskIndex() == 1) {
                        // OOka
                        final Map map = Server.getMapById(56);
                        val place = map.getFreeArea();
                        val npc = Ninja.getNinja("Thầy Ookamesama");
                        npc.p = new User();
                        npc.p.nj = npc;
                        npc.isNpc = true;
                        npc.setTypepk(Constants.PK_DOSAT);
                        p.nj.enterSamePlace(place, npc);
                        return;
                    } else if (ninja.getTaskIndex() == 2) {
                        // Haru
                        final Map map = Server.getMapById(0);
                        val place = map.getFreeArea();
                        val npc = Ninja.getNinja("Thầy Kazeto");
                        if (npc == null) {
                            System.out.println("KO THẦY ĐỐ MÀY LÀM NÊN");
                            return;
                        }
                        npc.p = new User();
                        npc.isNpc = true;
                        npc.p.nj = npc;
                        npc.setTypepk(Constants.PK_DOSAT);
                        p.nj.enterSamePlace(place, npc);
                        return;
                    } else if (ninja.getTaskIndex() == 3) {
                        final Map map = Server.getMapById(73);

                        val npc = Ninja.getNinja("Cô Toyotomi");
                        if (npc == null) {
                            System.out.println("KO THẦY ĐỐ MÀY LÀM NÊN");
                            return;
                        }
                        npc.isNpc = true;
                        npc.p = new User();
                        npc.setTypepk(Constants.PK_DOSAT);
                        npc.p.nj = npc;
                        val place = map.getFreeArea();
                        p.nj.enterSamePlace(place, npc);
                        return;
                    }
                } else if (ninja.getTaskId() == 15
                        && ninja.getTaskIndex() >= 1) {
                    if (menuId == -1) {
                        // Nhiem vu giao thu
                        if (ninja.getTaskIndex() == 1 && npcId == 14) {
                            p.nj.removeItemBags(214, 1);
                        } else if (ninja.getTaskIndex() == 2 && npcId == 15) {
                            p.nj.removeItemBags(214, 1);
                        } else if (ninja.getTaskIndex() == 3 && npcId == 16) {
                            p.nj.removeItemBags(214, 1);
                        }
                    }

                }
            }
        }

        m.cleanup();
        Label_6355:
        {
            label:
            switch (p.typemenu) {
                case 0: {
                    if (menuId == 0) {
                        // Mua vu khi
                        p.openUI(2);
                        break;
                    }
                    switch (menuId) {
                        case 1:
                            if (optionId == 0) {
                                // Thanh lap gia toc
                                if (!p.nj.clan.clanName.isEmpty()) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại con đã có gia tộc không thể thành lập thêm được nữa.");
                                    break label;
                                }
                                if (p.luong < ClanManager.LUONG_CREATE_CLAN) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Để thành lập gia tộc con cần phải có đủ 20.000 lượng trong người.");
                                    break label;
                                }
                                this.sendWrite(p, (short) 50, "Tên gia tộc");
                            } else if (optionId == 1) {
                                // Lanhdiagiatoc
                                if (p.getClanTerritoryData() == null) {
                                    if (p.nj.clan.typeclan == TOC_TRUONG) {

                                        if (p.nj.getAvailableBag() == 0) {
                                            p.sendYellowMessage("Hành trang không đủ để nhận chìa khoá");
                                            return;
                                        }
                                        val clan = ClanManager.getClanByName(p.nj.clan.clanName);
                                        if (clan.openDun <= 0) {
                                            p.sendYellowMessage("Số lần đi lãnh địa gia tộc đã hết vui lòng dùng thẻ bài hoặc đợi vào tuần");
                                            return;
                                        }

                                        val clanTerritory = new ClanTerritory(clan);
                                        Server.clanTerritoryManager.addClanTerritory(clanTerritory);
                                        p.setClanTerritoryData(new ClanTerritoryData(clanTerritory, p.nj));
                                        Server.clanTerritoryManager.addClanTerritoryData(p.getClanTerritoryData());

                                        clanTerritory.clanManager.openDun--;
                                        if (clanTerritory == null) {
                                            p.sendYellowMessage("Có lỗi xẩy ra");
                                            return;
                                        }
                                        val area = clanTerritory.getEntrance();
                                        if (area != null) {
                                            val item = ItemData.itemDefault(260);
                                            p.nj.addItemBag(false, item);
                                            if (p.getClanTerritoryData().getClanTerritory() != null) {

                                                if (p.getClanTerritoryData().getClanTerritory() != null) {
                                                    p.getClanTerritoryData().getClanTerritory().enterEntrance(p.nj);
                                                }

                                                clanTerritory.clanManager.informAll("Tộc trưởng đã mở lãnh địa gia tộc");
                                            } else {
                                                p.sendYellowMessage("Null sml");
                                            }
                                        } else {
                                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại lãnh địa gia tộc không còn khu trống");
                                        }

                                    } else {
                                        p.sendYellowMessage("Chỉ những người ưu tú được tộc trưởng mời mới có thể vào lãnh địa gia tộc");
                                    }
                                } else {
                                    val data = p.getClanTerritoryData();
                                    if (data != null) {
                                        val teri = data.getClanTerritory();
                                        if (teri != null) {
                                            teri.enterEntrance(p.nj);
                                        }
                                    }
                                }

                            } else if (optionId == 2) {
                                if (p.nj.quantityItemyTotal(262) < 500) {
                                    p.sendYellowMessage("Hành trang của con không có đủ 500 đồng tiền gia tộc");
                                } else if (p.nj.getAvailableBag() == 0) {
                                    p.sendYellowMessage("Hành trang không đủ chỗ trống");
                                } else {
                                    Item it = ItemData.itemDefault(263);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(262, 500);
                                }
                            } else if (optionId == 3) {
                                if (p.nj.clan.typeclan != TOC_TRUONG) {
                                    p.session.sendMessageLog("Mày không phải là tộc trưởng");
                                    break;
                                }
                                Service.startYesNoDlg(p, (byte) 6, "Bạn có chắc chắn muốn hủy gia tộc không?");
                                break;
                            }
                            break label;
                        case 2:
                            if (menuId != 2) {
                                break label;
                            }
                            if (p.nj.isNhanban) {
                                p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                                return;
                            }
                            if (optionId == 0) {
                                Service.evaluateCave(p.nj);
                                break label;
                            }
                            Cave cave = null;
                            if (p.nj.caveID != -1) {
                                if (Cave.caves.containsKey(p.nj.caveID)) {
                                    cave = Cave.caves.get(p.nj.caveID);
                                    p.nj.getPlace().leave(p);
                                    cave.map[0].area[0].EnterMap0(p.nj);
                                }
                            } else if (p.nj.party != null && p.nj.party.cave == null && p.nj.party.master != p.nj.id) {
                                p.session.sendMessageLog("Chỉ có nhóm trưởng mới được phép mở cửa hang động");
                                return;
                            }
                            if (cave == null) {
                                if (p.nj.nCave <= 0) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Số lần vào hang động cảu con hôm nay đã hết hãy quay lại vào ngày mai.");
                                    return;
                                }
                                if (optionId == 1) {
                                    if (p.nj.getLevel() < 30 || p.nj.getLevel() > 39) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 30 || p.nj.party.ninjas.get(i).getLevel() > 39) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(3);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(3);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 2) {
                                    if (p.nj.getLevel() < 40 || p.nj.getLevel() > 49) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 40 || p.nj.party.ninjas.get(i).getLevel() > 49) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(4);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(4);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 3) {
                                    if (p.nj.getLevel() < 50 || p.nj.getLevel() > 59) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 50 || p.nj.party.ninjas.get(i).getLevel() > 59) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(5);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(5);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 4) {
                                    if (p.nj.getLevel() < 60 || p.nj.getLevel() > 69) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null && p.nj.party.ninjas.size() > 1) {
                                        p.session.sendMessageLog("Hoạt động lần này chỉ được phép một mình");
                                        return;
                                    }
                                    cave = new Cave(6);
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 5) {
                                    if (p.nj.getLevel() < 70 || p.nj.getLevel() > 89) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 70) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(7);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(7);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 6) {
                                    if (p.nj.getLevel() < 90 || p.nj.getLevel() > 150) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }

                                    if (p.nj.party != null && p.nj.party.getKey() != null
                                            && p.nj.party.getKey().get().getLevel() >= 90) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 90 || p.nj.party.ninjas.get(i).getLevel() > 151) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }

                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(9);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(9);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (cave != null) {
                                    final Ninja c = p.nj;
                                    --c.nCave;
                                    p.nj.pointCave = 0;
                                    p.nj.getPlace().leave(p);
                                    cave.map[0].area[0].EnterMap0(p.nj);
                                }
                            }
                            p.setPointPB(p.nj.pointCave);
                            break label;
                        case 3: {
                            if (optionId == 0) {
                                // Thach dau loi dai
                                this.sendWrite(p, (short) 2, "Nhập tên đối thủ của ngươi vào đây");
                                //if ((p.nj.getTaskId() == 42 && p.nj.getTaskIndex() == 1)) {                                                
                                //p.nj.upMainTask();
                                //}
                                break;
                            } else if (optionId == 1) {
                                // Xem thi dau
                                Service.sendBattleList(p);
                            }
                        }
                        break label;
                        case 4:
                            Random generator = new Random();
                            int value = generator.nextInt(3);
                            if (value == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ở chỗ ta có rất nhiều binh khí có giá trị");
                            }
                            if (value == 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chọn cho mình một món bình khí đi.");
                            }
                            if (value == 2) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Haha, nhà ngươi cần vũ khí gì?");
                            }
                            break label;
                    }
                }
                case 1: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            p.openUI(21 - p.nj.gender);
                            break;
                        }
                        if (optionId == 1) {
                            p.openUI(23 - p.nj.gender);
                            break;
                        }
                        if (optionId == 2) {
                            p.openUI(25 - p.nj.gender);
                            break;
                        }
                        if (optionId == 3) {
                            p.openUI(27 - p.nj.gender);
                            break;
                        }
                        if (optionId == 4) {
                            p.openUI(29 - p.nj.gender);
                            break;
                        }
                    } else if (menuId == 1) {
                        Random generator = new Random();
                        int value = generator.nextInt(3);
                        if (value == 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Giáp, giày giá rẻ đây!");
                        }
                        if (value == 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Không mặc giáp mua từ ta, ra khỏi trường ngươi sẽ gặp nguy hiểm.");
                        }
                        if (value == 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi cần giày, giáp sắt, quần áo?");
                        }
                        break label;
                    }
                    break;
                }
                case 2: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            p.openUI(16);
                            break;
                        } else if (optionId == 1) {
                            p.openUI(17);
                            break;
                        } else if (optionId == 2) {
                            p.openUI(18);
                            break;
                        } else if (optionId == 3) {
                            p.openUI(19);
                            break;
                        }
                    } else if (menuId == 1) {
                        ItemData data;
                        if (optionId == 0) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.getLevel() < 50) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Cấp độ của con cần đạt 50 để nhận nhiệm vụ này");
                                return;
                            }

                            if (p.nj.countTaskDanhVong < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lần nhận nhiệm vụ của con hôm nay đã hết");
                                return;
                            }

                            if (p.nj.isTaskDanhVong == 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Trước đó con đã nhận nhiệm vụ rồi, hãy hoàn thành đã nha");
                                return;
                            }

                            int type = DanhVongData.randomNVDV();
                            p.nj.taskDanhVong[0] = type;
                            p.nj.taskDanhVong[1] = 0;
                            p.nj.taskDanhVong[2] = DanhVongData.targetTask(type);
                            p.nj.isTaskDanhVong = 1;
                            p.nj.countTaskDanhVong--;
                            if (p.nj.isTaskDanhVong == 1) {
                                String nv = "NHIỆM VỤ LẦN NÀY: \n"
                                        + String.format(DanhVongData.nameNV[p.nj.taskDanhVong[0]],
                                                p.nj.taskDanhVong[1],
                                                p.nj.taskDanhVong[2])
                                        + "\n\n- Số lần nhận nhiệm vụ còn lại là: " + p.nj.countTaskDanhVong;
                                server.manager.sendTB(p, "Nhiệm vụ", nv);
                            }
                            break;
                        } else if (optionId == 1) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.isTaskDanhVong == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa nhận nhiệm vụ nào cả!");
                                return;
                            }

                            if (p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa hoàn thành nhiệm vụ ta giao!");
                                return;
                            }

                            if (p.nj.getAvailableBag() < 2) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không đủ chỗ trống để nhận thưởng");
                                return;
                            }

                            int point = 10;
                            if (p.nj.taskDanhVong[0] == 9) {
                                point = 20;
                            }

                            p.nj.isTaskDanhVong = 0;
                            p.nj.taskDanhVong = new int[]{-1, -1, -1, 0, p.nj.countTaskDanhVong};
                            Item item = ItemData.itemDefault(695);
                            item.quantity = 1;
                            item.isLock = true;
                            if (p.nj.pointUydanh < 5000) {
                                ++p.nj.pointUydanh;
                            }

                            p.nj.addItemBag(true, item);
                            int type = util.nextInt(10);

                            if (p.nj.avgPointDanhVong(p.nj.getPointDanhVong(type))) {
                                for (int i = 0; i < 10; i++) {
                                    type = i;
                                    if (!p.nj.avgPointDanhVong(p.nj.getPointDanhVong(type))) {
                                        break;
                                    }
                                }
                            }
                            p.nj.plusPointDanhVong(type, point);

                            if (p.nj.countTaskDanhVong % 2 == 0) {
                                Item itemUp = ItemData.itemDefault(p.nj.gender == 1 ? -1 : -1, true);
                                itemUp.isLock = true;
                                itemUp.isExpires = false;
                                itemUp.expires = -1L;
                                itemUp.quantity = util.nextInt(1, 2);
                                p.nj.addItemBag(true, itemUp);
                            } else {
                                Item itemUp = ItemData.itemDefault(p.nj.gender == 1 ? -1 : -1, true);
                                itemUp.isLock = true;
                                itemUp.isExpires = false;
                                itemUp.expires = -1L;
                                itemUp.quantity = util.nextInt(1, 2);
                                p.nj.addItemBag(true, itemUp);
                            }

                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con hãy nhận lấy phần thưởng của mình.");
                            break;
                        } else if (optionId == 2) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.isTaskDanhVong == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa nhận nhiệm vụ nào cả!");
                                return;
                            }

                            Service.startYesNoDlg(p, (byte) 2, "Con có chắc chắn muốn huỷ nhiệm vụ lần này không?");
                            break;
                        } else if (optionId == 3) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.checkPointDanhVong(1)) {
                                if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không đủ chỗ trống để nhận thưởng");
                                    return;
                                }

                                Item item = ItemData.itemDefault(685, true);
                                item.quantity = 1;
                                item.upgrade = 1;
                                item.isLock = true;
                                Option op = new Option(6, 1000);
                                item.option.add(op);
                                op = new Option(87, 500);
                                item.option.add(op);
                                p.nj.addItemBag(false, item);
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ điểm để nhận mắt");
                            }

                            break;
                        } else if (optionId == 4) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.ItemBody[14] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy đeo mắt vào người trước rồi nâng cấp nhé.");
                                return;
                            }

                            if (p.nj.ItemBody[14] == null) {
                                return;
                            }

                            if (p.nj.ItemBody[14].upgrade >= 10) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Mắt của con đã đạt cấp tối đa");
                                return;
                            }

                            if (!p.nj.checkPointDanhVong(p.nj.ItemBody[14].upgrade)) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ điểm danh vọng để thực hiện nâng cấp");
                                return;
                            }

                            data = ItemData.ItemDataId(p.nj.ItemBody[14].id);
                            Service.startYesNoDlg(p, (byte) 0, "Bạn có muốn nâng cấp " + data.name + " với " + GameScr.coinUpMat[p.nj.ItemBody[14].upgrade] + " yên hoặc xu với tỷ lệ thành công là " + GameScr.percentUpMat[p.nj.ItemBody[14].upgrade] + "% không?");
                            break;
                        } else if (optionId == 5) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.ItemBody[14] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy đeo mắt vào người trước rồi nâng cấp nhé.");
                                return;
                            }

                            if (p.nj.ItemBody[14].upgrade >= 10) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Mắt của con đã đạt cấp tối đa");
                                return;
                            }

                            if (!p.nj.checkPointDanhVong(p.nj.ItemBody[14].upgrade)) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ điểm danh vọng để thực hiện nâng cấp");
                                return;
                            }

                            data = ItemData.ItemDataId(p.nj.ItemBody[14].id);
                            Service.startYesNoDlg(p, (byte) 1, "Bạn có muốn nâng cấp " + data.name + " với " + GameScr.coinUpMat[p.nj.ItemBody[14].upgrade] + " yên hoặc xu và " + GameScr.goldUpMat[p.nj.ItemBody[14].upgrade] + " lượng với tỷ lệ thành công là " + GameScr.percentUpMat[p.nj.ItemBody[14].upgrade] * 2 + "% không?");
                            break;
                        } else if (optionId == 6) {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            String nv = "- Hoàn thành nhiệm vụ. Hãy gặp Ameji để trả nhiệm vụ.\n- Hôm nay có thể nhận thêm " + p.nj.countTaskDanhVong + " nhiệm vụ trong ngày.\n- Hôm nay có thể sử dụng thêm " + p.nj.useDanhVongPhu + " Danh Vọng Phù để nhận thêm 5 lần làm nhiệm vụ.\n- Hoàn thành nhiệm vụ sẽ nhận 1 viên đá danh vọng cấp 1.\n- Khi đủ mốc 100 điểm mỗi loại có thể nhận mắt và nâng cấp mắt.";
                            if (p.nj.isTaskDanhVong == 1) {
                                nv = "NHIỆM VỤ LẦN NÀY: \n" + String.format(DanhVongData.nameNV[p.nj.taskDanhVong[0]], p.nj.taskDanhVong[1], p.nj.taskDanhVong[2]) + "\n\n" + nv;
                            }

                            server.manager.sendTB(p, "Nhiệm vụ", nv);
                            break;
                        }
                    } else if (menuId == 2) {
                        if (p.nj.ItemBody[14] == null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng mắt Geningan để hủy.");
                            break;
                        }
                        if (p.nj.ItemBody[14].id != 685) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng mắt Geningan để hủy.");
                            break;
                        }
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                            break;
                        }
                        p.nj.removeItemBody((byte) 14);
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn đã hủy mắt Geningan thành công");
                        break;
                    } else if (menuId == 3) {
                        Random generator = new Random();
                        int value = generator.nextInt(3);
                        if (value == 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con chọn loại trang sức gì nào?");
                        }
                        if (value == 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Trang sức không chỉ để ngắm, nó còn tăng sức mạnh của con");
                        }
                        if (value == 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần mua ngọc bội, nhẫn, dây chuyền, bùa họ thân à?");
                        }
                        break label;
                    }
                }
                break;
                case 3: {
                    if (menuId == 0) {
                        p.openUI(7);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(6);
                        break;
                    }
                    if (menuId == 2) {
                        int num = util.nextInt(0, 1);

                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Mua ngay HP,MP từ ta, được chế tạo từ loại thảo dược quý hiếm nhất");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đi đường cần mang theo ít dược phẩm");
                                break;
                        }
                    }
                }
                break;
                case 4: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(9);
                            break;
                        }
                        case 1: {
                            p.openUI(8);
                            break;
                        }
                        case 2: {
                            int num = util.nextInt(0, 2);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ăn xong đảm bảo ngươi sẽ quay lại lần sau");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Thức ăn của ta là ngon nhất rồi");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hahaha, chắc ngươi đi đường cũng mệt rồi");
                                    break;
                            }
                        }
                        break;
                        case 3: {
                            switch (optionId) {
                                case 0: {
                                    // Đăng kí thien dia bang
                                    RegisterResult result = null;
                                    if (p.nj.get().getLevel() <= 80) {
                                        result = GeninTournament.gi().register(p);

                                    } else if (p.nj.get().getLevel() > 80 && p.nj.get().getLevel() <= 150) {
                                        result = KageTournament.gi().register(p);
                                    }

                                    if (result != null) {
                                        if (result == RegisterResult.SUCCESS) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã đăng kí thành công");
                                        } else if (result == RegisterResult.ALREADY_REGISTER) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã đăng kí thành công rồi");
                                        } else if (result == RegisterResult.LOSE) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã thua không thể đăng kí được");
                                        }
                                    } else {

                                    }
                                    break;
                                }
                                case 1: {
                                    //Chinh phuc thien dia bang
                                    try {
                                        final List<TournamentData> tournaments = getTypeTournament(p.nj.getLevel()).getChallenges(p);
                                        Service.sendChallenges(tournaments, p);
                                    } catch (Exception e) {

                                    }

                                    break;
                                }
                                case 2: {
                                    //Thien bang
                                    sendThongBaoTDB(p, KageTournament.gi(), "Thiên bảng\n");
                                    break;
                                }
                                case 3: {
                                    // Dia bang
                                    sendThongBaoTDB(p, GeninTournament.gi(), "Địa bảng\n");
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 699: {
                    switch (menuId) {
                        case 0: {
                            Service.openMenuBox(p);
                            break;
                        }
                        case 1: {
                            Service.openMenuBST(p);
                            break;
                        }
                        case 2: {
                            Service.openMenuCaiTrang(p);
                            break;
                        }
                        case 3: {
                            //Tháo cải trang
                            if (p.nj.ItemBodyHide[0] != null) {
                                Service.thaoCaiTrang(p);
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    switch (menuId) {
                        case 0: {
                            p.typemenu = ((optionId == 0) ? 699 : 700);
                            doMenuArray(p, new String[]{"Mở rương", "Mở bộ sưu tập", "Cải trang", "Tháo cải trang"});
                            break;
                        }
                        case 1: {
                            p.nj.mapLTD = p.nj.getPlace().map.id;
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Lưu tọa độ thành công, khi kiệt sức con sẽ được khiêng về đây");
                            break;
                        }
                        case 2: {
                            if (optionId != 0) {
                                break;
                            }
                            if (p.nj.isNhanban) {
                                if (p.nj.clone.getEffId(34) == null) {
                                    p.nj.getPlace().chatNPC(p, (short) 5, "Phải dùng thí luyện thiếp mới có thể vào được");
                                    return;
                                }
                            }
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Chức năng yêu cầu trình độ 60");
                                return;
                            }
                            final Manager manager = this.server.manager;
                            final Map ma = Manager.getMapid(139);
                            for (final Place area : ma.area) {
                                if (area.getNumplayers() < ma.template.maxplayers) {
                                    p.nj.getPlace().leave(p);
                                    area.EnterMap0(p.nj);
                                    return;
                                }
                            }
                            break;
                        }
                        case 3: {
                            Service.openMenuGiaHan(p, 5);
                            break;
                        }
                        case 4: {
                            p.nj.getPlace().chatNPC(p, (short) 5, "Không cóa đâu!");
                            break;
                        }
                        case 5: {
                            int num = util.nextInt(0, 2);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ta giữ đồ chưa hề để thất lạc bao giờ.");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy an tâm giao đồ cho ta nào!");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trên người của ngươi toàn là đồ có giá trị, sao không cất bớt ở đây?");
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 6: {
                    switch (menuId) {
                        case 0: {
                            if (optionId == 0) {
                                p.openUI(10);
                                break;
                            }
                            if (optionId == 1) {
                                p.openUI(31);
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if (optionId == 0) {
                                p.openUI(12);
                                break;
                            }
                            if (optionId == 1) {
                                p.openUI(11);
                                break;
                            }
                            break;
                        }
                        case 2: {
                            p.openUI(13);
                            break;
                        }
                        case 3: {
                            p.openUI(33);
                            break;
                        }
                        case 4: {
                            // Luyen ngoc
                            p.openUI(46);
                            break;
                        }
                        case 5: {
                            // Kham ngoc
                            p.openUI(47);
                            break;
                        }
                        case 6: {
                            // Got ngoc
                            p.openUI(49);
                            break;
                        }
                        case 7: {
                            // Thao ngoc
                            p.openUI(50);
                            break;
                        }
                        case 8: {
                            int num = util.nextInt(0, 3);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi muốn cải tiến trang bị?");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Nâng cấp trang bị:Uy tín, giá cả phải chăng.");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đảm bảo sau khi nâng cấp đồ của ngươi sẽ tốt hơn hẳn");
                                    break;
                                case 3:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đập đồ không bịp như sever khác đâu");
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 7: {
                    if (menuId == 0) {
                        int num = util.nextInt(0, 2);

                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Nhà ngươi muốn đi đâu?");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đi xe kéo của ta an toàn là số một.");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngựa của ta rất khỏe, có thể chạy ngàn dặm");
                                break;
                        }
                        break;
                    }
                    if (menuId > 0 && menuId <= Map.arrLang.length) {
                        final Map ma = Manager.getMapid(Map.arrLang[menuId - 1]);
                        for (final Place area : ma.area) {
                            if (area.getNumplayers() < ma.template.maxplayers) {
                                p.nj.getPlace().leave(p);
                                area.EnterMap0(p.nj);
                                return;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 8: {
                    if (menuId == 3) {
                        int num = util.nextInt(0, 2);

                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Nhà ngươi muốn đi đâu?");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đi xe kéo của ta an toàn là số một.");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngựa của ta rất khỏe, có thể chạy ngàn dặm");
                                break;
                        }
                        break;
                    }
                    if (menuId >= 0 && menuId < Map.arrTruong.length) {
                        final Map ma = Manager.getMapid(Map.arrTruong[menuId]);
                        for (final Place area : ma.area) {
                            if (area.getNumplayers() < ma.template.maxplayers) {
                                p.nj.getPlace().leave(p);
                                area.EnterMap0(p.nj);
                                return;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 9: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.session.sendMessageLog("Còn cần đạt trình độ cấp 10");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(420));
                        if (optionId == 0) {
                            p.Admission((byte) 1);
                        } else if (optionId == 1) {
                            p.Admission((byte) 2);
                        }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                        break;
                    }
                    if (menuId == 3) {
                        int num = util.nextInt(0, 2);
                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Theo học ở đây là vinh hạnh của ngươi, biết chứ?");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi muốn trở thành hỏa Ninja thì học, không thì cút!");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Trường ta dạy kiếm và phi tiêu, chẳng dạy các thứ vô danh khác.");
                                break;
                        }
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 1 && p.nj.get().nclass != 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            if (p.nj.taytn < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lượt tẩy điểm tiềm năng của con đã hết");
                                break;
                            } else {
                                p.restPpoint(p.nj.get());
                                p.nj.taytn -= 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                                break;
                            }
                        }
                        if (optionId == 1) {
                            if (p.nj.taykn < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lượt tẩy điểm kỹ năng của con đã hết");
                                break;
                            } else {
                                p.restSpoint();
                                p.nj.taykn -= 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                                break;
                            }
                        }
                        break;
                    }
                }
                case 10: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.session.sendMessageLog("Còn cần đạt trình độ cấp 10");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(421));
                        if (optionId == 0) {
                            p.Admission((byte) 3);
                        } else if (optionId == 1) {
                            p.Admission((byte) 4);
                        }
                        p.nj.getPlace().chatNPC(p, (short) 9, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                        break;
                    }
                    if (menuId == 3) {
                        int num = util.nextInt(0, 2);
                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con có cảm thấy lạnh không?");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Tập trung học tốt nhé con.");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Học, để thành tài, để thành người tốt, chứ không phải để ganh đua với đời.");
                                break;
                        }
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 3 && p.nj.get().nclass != 4) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            if (p.nj.taytn < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lượt tẩy điểm tiềm năng của con đã hết");
                                break;
                            } else {
                                p.restPpoint(p.nj.get());
                                p.nj.taytn -= 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                                break;
                            }
                        }
                        if (optionId == 1) {
                            if (p.nj.taykn < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lượt tẩy điểm kỹ năng của con đã hết");
                                break;
                            } else {
                                p.restSpoint();
                                p.nj.taykn -= 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                                break;
                            }
                        }
                        break;
                    }
                }
                case 11: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.session.sendMessageLog("Còn cần đạt trình độ cấp 10");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(422));
                        if (optionId == 0) {
                            p.Admission((byte) 5);
                        } else if (optionId == 1) {
                            p.Admission((byte) 6);
                        }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                        break;
                    }
                    if (menuId == 3) {
                        int num = util.nextInt(0, 2);
                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi may mắn mới gặp được ta đó, ta vốn là Thần Gió mà!");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "So với các trường khác, trường Gió của chúng ta là tốt nhất");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Một học sinh trường gió chúng ta có thể chấp hai học sinh các trường kia.");
                                break;
                        }
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 5 && p.nj.get().nclass != 6) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            if (p.nj.taytn < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lượt tẩy điểm tiềm năng của con đã hết");
                                break;
                            } else {
                                p.restPpoint(p.nj.get());
                                p.nj.taytn -= 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                                break;
                            }
                        }
                        if (optionId == 1) {
                            if (p.nj.taykn < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Số lượt tẩy điểm kỹ năng của con đã hết");
                                break;
                            } else {
                                p.restSpoint();
                                p.nj.taykn -= 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                                break;
                            }
                        }
                        break;
                    }
                }
                case 12: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            server.manager.sendTB(p, "Trưởng làng", "Dùng các phím Q,W,E,A,D: Di chuyển nhân vật\nHoặc các phím Lên,Trái,Phải: Di chuyển nhân vật\nPhím Spacebar hoặc phím Enter: Tấn công hoặc hành động\nPhím F1: Menu,Phím F2: Đổi mục tiêu, phím 6,7: Dùng bình HP,MP\nPhím 0: Chat,Phím P: Đổi kỹ năng,Phím 1,2,3,4,5: Sử dụng kỹ năng được gán trước trong mục Kỹ Năng");
                            break;
                        }
                        if (optionId == 1) {
                            server.manager.sendTB(p, "Trưởng làng", "Kiếm, Kunai, Đao: Ưu tiên tăng sức mạnh(sức đánh) --> thể lực(HP) --> Thân pháp(Né đòn, chính xác) --> Charka(MP).\n\nTiêu, Cung, Quạt: Ưu tiên tăng Charka(Sức đánh, MP) -->thể lực(HP)--> Thân pháp(Né đòn, chính xác). Không tăng SM.");
                            break;
                        }
                        if (optionId == 2) {
                            server.manager.sendTB(p, "Trưởng làng", "Pk thường: trạng thái hòa bình.\n\nPk phe: không đánh được người cùng nhóm hay cùng bang hội. Giết người không lên điểm hiếu chiến.\n\nPk đồ sát: có thể đánh tất cả người chơi. Giết 1 người sẽ lên 1 điểm hiếu chiến.\n\nĐiểm hiếu chiến cao sẽ không sử dụng bình HP, MP, Thức ăn.\n\nTỷ thí: chọn người chơi, chọn tỷ thí, chờ người đó đồng ý.\n\nCừu Sát: Chọn người chơi khác, chọn cừu sát, điểm hiếu chiến tăng 2.");
                            break;
                        }
                        if (optionId == 3) {
                            server.manager.sendTB(p, "Trưởng làng", "Bạn có thể tạo một nhóm tối đa 6 người chơi.\n\nNhững người trong cùng nhóm sẽ được nhận thêm x% điểm kinh nghiệm từ người khác.\n\nNhững người cùng nhóm sẽ cùng được vật phẩm, thành tích nếu cùng chung nhiệm vụ.\n\nĐể mời người vào nhóm, chọn người đó, và chọn mời vào nhóm. Để quản lý nhóm, chọn Menu/Tính năng/Nhóm.");
                            break;
                        }
                        if (optionId == 4) {
                            server.manager.sendTB(p, "Trưởng làng", "Đá dùng để nâng cấp trang bị. Bạn có thể mua từ cửa hàng hoặc nhặt khi đánh quái.Nâng cấp đá nhằm mục đích nâng cao tỉ lệ thành công khi nâng cấp trang bị cấp cao.Để luyện đá, bạn cần tìm Kenshinto.\n\nĐể đảm bảo thành công 100%, 4 viên đá cấp thấp sẽ luyện thành 1 viên đá cấp cao hơn.");
                            break;
                        }
                        if (optionId == 5) {
                            server.manager.sendTB(p, "Trưởng làng", "Nâng cấp trang bị nhằm mục đích gia tăng các chỉ số cơ bản của trang bị. Có các cấp trang bị sau +1, +2, +3... tối đa +16.Để thực hiện, bạn cần gặp NPC Kenshinto. Sau đó, tiến hành chọn vật phẩm và số lượng đá đủ để nâng cấp. Lưu ý, trang bị cấp độ 5 trở lên nâng cấp thất bại sẽ bị giảm cấp độ.\n\nBạn có thể tách một vật phẩm đã nâng cấp và thu lại 50% số đá đã dùng để nâng cấp trang bị đó.");
                            break;
                        }
                        if (optionId == 6) {
                            server.manager.sendTB(p, "Trưởng làng", "Khi tham gia các hoạt động trong game bạn sẽ nhận được điểm hoạt động. Qua một ngày điểm hoạt động sẽ bị trừ dần (nếu từ 1-49 trừ 1, 50-99 trừ 2, 100-149 trừ 3...). Mỗi tuần bạn sẽ có cơ hội đổi Yên sang Xu nếu có đủ điểm hoạt động theo vêu cầu của NPC Okanechan.\n\nMột tuần một lần duy nhất được đối tối đa 70.000 Yên = 70.000 xu.");
                            break;
                        }
                    }
                    if (menuId == 1) {
                        Random generator = new Random();
                        int value = generator.nextInt(3);
                        if (value == 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Làng Tone là ngôi làng cổ xưa, đã có từ rất lâu.");
                        }
                        if (value == 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đi thưa, về trình, nhé các con");
                        }
                        if (value == 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta là Tajima, mọi việc ở đây đều do ta quản lý.");
                        }
                        break;
                    }
                    if (menuId == 5) {
                        if (p.quatop == 1) {
                            p.session.sendMessageLog("Ngươi đã nhận quà rồi!");
                        } else if (p.nj.isNhanban) {
                            p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                        } else if (p.nj.getAvailableBag() < 2) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        } else {
                            //quaduatop
                            if (p.id == 0) {//top1
                                Item it = ItemData.itemDefault(800 - p.nj.gender);
                                it.isExpires = false;
                                it.expires = -1;
                                p.nj.addItemBag(true, it);
                                Item it1 = ItemData.itemDefault(839);
                                it1.isExpires = false;
                                it1.expires = -1;
                                p.nj.addItemBag(true, it1);
                                p.quatop = 1;
                                return;
                            } else if (p.id == 0) {//top2
                                Item it = ItemData.itemDefault(800 - p.nj.gender);
                                it.isExpires = false;
                                it.expires = -1;
                                p.nj.addItemBag(true, it);
                                Item it1 = ItemData.itemDefault(839);
                                it1.isExpires = false;
                                it1.expires = -1;
                                p.nj.addItemBag(true, it1);
                                p.quatop = 1;
                                return;
                            } else if (p.id == 0) {//top3
                                Item it = ItemData.itemDefault(800 - p.nj.gender);
                                it.isExpires = true;
                                it.expires = util.TimeDay(30);
                                p.nj.addItemBag(true, it);
                                Item it1 = ItemData.itemDefault(839);
                                it1.isExpires = true;
                                it1.expires = util.TimeDay(30);
                                p.nj.addItemBag(true, it1);
                                p.quatop = 1;
                                return;
                            } else if (p.id == 0 || p.id == 0 || p.id == 0) {//top4-10
                                Item it = ItemData.itemDefault(800 - p.nj.gender);
                                it.isExpires = true;
                                it.expires = util.TimeDay(14);
                                p.nj.addItemBag(true, it);
                                Item it1 = ItemData.itemDefault(839);
                                it1.isExpires = true;
                                it1.expires = util.TimeDay(14);
                                p.nj.addItemBag(true, it1);
                                p.quatop = 1;
                                return;
                            }
//                                else if (p.id == 0 || p.id == 0 || p.id == 0 || p.id == 0) {//top7-10
//                                Item it = ItemData.itemDefault(800 - p.nj.gender);  
//                                it.isExpires = true;
//                                it.expires = util.TimeDay(7);
//                                p.nj.addItemBag(true, it);
//                                Item it1 = ItemData.itemDefault(839); 
//                                it1.isExpires = true;
//                                it1.expires = util.TimeDay(7);
//                                p.nj.addItemBag(true, it1);
//                                p.quatop = 1;
//                                return;
//                            }
                            break;
                        }
                    }
                    if (menuId == 3) {
                        //p.session.sendMessageLog("Tạm bảo trì phân thân");
                        if (p.nj.timeRemoveClone > System.currentTimeMillis()) {
                            p.toNhanBan();
                            break;
                        }
                        break;
                    } else {
                        if (menuId != 4) {
                            if (menuId == 2) {
                                p.nj.clearTask();
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã huỷ hết nhiệm vụ và vật phẩm nhiệm vụ của con lần sau làm nhiệm vụ tốt hơn nhé");
                                Service.getTask(p.nj);
                                break;
                            }
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đang thực hiện nhiệm vụ kiên trì diệt ác, hãy chọn Menu/Nhiệm vụ để biết mình đang làm đến đâu");
                            break;
                        }
                        if (!p.nj.clone.isDie && p.nj.timeRemoveClone > System.currentTimeMillis()) {
                            p.exitNhanBan(false);
                            p.nj.clone.open(p.nj.timeRemoveClone, p.nj.getPramSkill(71));
                            break;
                        }
                        break;
                    }
                }

                case 14:
                case 15:
                case 16: {
                    boolean hasItem = false;
                    for (Item item : p.nj.ItemBag) {
                        if (item != null && item.id == 214) {
                            hasItem = true;
                            break;
                        }
                    }
                    if (hasItem) {
                        p.nj.removeItemBags(214, 1);
                        p.nj.getPlace().chatNPC(p, npcId, "Ta rất vui vì cô béo còn quan tâm đến ta.");
                        p.nj.upMainTask();
                    } else {
                        if (p.nj.getTaskId() == 20 && p.nj.getTaskIndex() == 1 && npcId == 15) {
                            p.nj.getPlace().leave(p);
                            final Map map = Server.getMapById(74);
                            val place = map.getFreeArea();
                            synchronized (place) {
                                p.expiredTime = System.currentTimeMillis() + 600000L;
                            }
                            Service.batDauTinhGio(p, 600);
                            place.refreshMobs();
                            place.EnterMap0(p.nj);
                        } else {
                            p.nj.getPlace().chatNPC(p, npcId, "Không có thư để con giao");
                        }
                    }
                    break;
                }
                case 17: {
                    val jaien = Ninja.getNinja("Jaian");
                    jaien.p = new User();
                    jaien.p.nj = jaien;
                    val place = p.nj.getPlace();
                    jaien.upHP(jaien.getMaxHP());
                    jaien.isDie = false;

                    jaien.x = place.map.template.npc[0].x;
                    jaien.id = -p.nj.id;
                    jaien.y = place.map.template.npc[0].y;
                    place.Enter(jaien.p);
                    Place.sendMapInfo(jaien.p, place);
                    break;
                }
                //NPC Cay thong
//            case 39:
//                switch (menuId) {
//                case 0: {
//                    if (p.nj.getAvailableBag() == 0) {
//                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
//                        return;
//                    } else if (p.nj.quantityItemyTotal(645) < 1) {
//                        p.session.sendMessageLog("Bạn không đủ túi lộc đầu xuân");
//                        return;
//                    } else {                        
//                        short[] arId = new short[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8 , 8, 8, 8, 8, 8, 8 ,8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 ,9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 340, 340, 383, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453 ,337, 338, 567, 477, 477, 684, 684, 788, 788, 789, 789 , 778, 778, 778, 778, 778, 778, 778};
//                        short idI = arId[util.nextInt(arId.length)];
//                        Item itemup = ItemData.itemDefault(idI);
//                        itemup.isLock = false;
//                        //itemup.expires = util.TimeDay(7);
//                        p.nj.addItemBag(true, itemup);
//                        p.nj.topSK2 += 1;
//                    }
//                    p.nj.removeItemBags(645, 1);
//                }
//                break;
//                case 1: {
//                            this.server.manager.sendTB(p, "Hướng Dẫn", "Để tham gia đua top các bạn hãy tham gia chức năng may mắn đầu xuân tại npc Cây mai \n\nMỗi lần tham gia may mắn đầu xuân các bạn sẽ được 1 điểm tương ứng + 1 phần quà ngẫu nhiên \n\nTham gia box chat của sever để xem chi tiết phần quà đua top");
//                            break;
//                        }
//                case 2: {
//                            this.server.manager.sendTB(p, "Top", BXHManager.getStringBXH(4));
//                            break;
//                        }
//                case 3: {
//                            this.server.manager.sendTB(p, "Top vui xuân", BXHManager.getStringBXH(5));
//                            break;
//                        }
//                }
//                break;
//                case 35: {
//                    switch (menuId) {
//                        //case 0: {
//                            //this.server.manager.sendTB(p, "Bảng Tin", "1. Sever sẽ open sự kiện tết nguyên đán");
//                            //break;
//                        //}
//                        case 0: {
//                            if (p.nj.quantityItemyTotal(484) < 5000) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 5000 bít tất may mắn");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(836);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(484, 5000);
//                                break;
//                            }
//                        }
//                        case 1: {
//                            if (p.nj.quantityItemyTotal(582) < 7000) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 7000 pháo hoa");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(846);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(582, 7000);
//                                break;                        
//                            }    
//                        }
//                    }
//                }
//                break;
                case 40: {
                    switch (menuId) {
                        case 0: {
                            switch (optionId) {
                                case 0: {
                                    if (p.nj.isNhanban) {
                                        p.session.sendMessageLog("Phân thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    if (p.nj.ItemBody[15] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng bí kíp ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() <= 6) {
                                p.session.sendMessageLog("ít nhất dư 6 ô trong hành trang mới sử dụng được tính năng này");
                                break;
                            }
                                    if (p.luong < 500) {
                                        p.session.sendMessageLog("Bạn không có đủ 500 lượng");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(396 + p.nj.nclass);
                                    int a = 0;
                                    for (int i = 0; i < GameScr.optionBikiep.length; i++) {
                                        if (util.nextInt(1, 10) < 3) {
                                            it.option.add(new Option(GameScr.optionBikiep[i], util.nextInt(GameScr.paramBikiep[i], GameScr.paramBikiep[i] * 70 / 100)));
                                            a++;
                                        }
                                    }
                                    it.setLock(true);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBody((byte) 15);
                                    p.upluongMessage(-500);
                                    String b = "";
                                    if (a > 5) {
                                        b = "Khá mạnh đó, ngươi thấy ta làm tốt không ?";
                                    } else if (a > 2) {
                                        b = "Không tệ, ngươi xem có ổn không ?";
                                    } else {
                                        b = "Ta chỉ giúp được cho ngươi đến thế thôi. Ta xin lỗi !";
                                    }
                                    p.nj.getPlace().chatNPC(p, (short) 40, b);
                                    break;
                                }
                                case 1: {
                                    if (p.nj.isNhanban) {
                                        p.session.sendMessageLog("Phân thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    if (p.nj.ItemBody[15] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng bí kíp ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                                        return;
                                    }
                                    if (p.nj.getAvailableBag() <= 6) {
                                p.session.sendMessageLog("ít nhất dư 6 ô trong hành trang mới sử dụng được tính năng này");
                                break;
                            }
                                    Item it = p.nj.ItemBody[15];
                                    if (p.nj.nclass == 1 || p.nj.nclass == 2) {
                                        Service.startYesNoDlg(p, (byte) 4, "Con có muốn nâng cấp bí kíp với " + GameScr.luongBikiep[it.getUpgrade()] + " lượng + " + GameScr.daBikiep[it.getUpgrade()] + " đá hỏa và tỷ lệ thành công là: " + GameScr.percentBikiep[it.getUpgrade()] + "% hay không?");
                                    } else if (p.nj.nclass == 3 || p.nj.nclass == 4) {
                                        Service.startYesNoDlg(p, (byte) 4, "Con có muốn nâng cấp bí kíp với " + GameScr.luongBikiep[it.getUpgrade()] + " lượng + " + GameScr.daBikiep[it.getUpgrade()] + "  đá băng và tỷ lệ thành công là: " + GameScr.percentBikiep[it.getUpgrade()] + "% hay không?");
                                    } else if (p.nj.nclass == 5 || p.nj.nclass == 6) {
                                        Service.startYesNoDlg(p, (byte) 4, "Con có muốn nâng cấp bí kíp với " + GameScr.luongBikiep[it.getUpgrade()] + " lượng + " + GameScr.daBikiep[it.getUpgrade()] + " đá gió và tỷ lệ thành công là: " + GameScr.percentBikiep[it.getUpgrade()] + "% hay không?");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                        case 1: {
                            switch (optionId) {
                                case 0: {
                                    if (p.nj.isHuman) {
                                        p.session.sendMessageLog("Chủ thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    if (p.nj.clone.ItemBody[15] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng bí kíp ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() <= 6) {
                                p.session.sendMessageLog("ít nhất dư 6 ô trong hành trang mới sử dụng được tính năng này");
                                break;
                            }
                                    if (p.luong < 500) {
                                        p.session.sendMessageLog("Bạn không có đủ 500 lượng");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(396 + p.nj.clone.nclass);
                                    int a = 0;
                                    for (int i = 0; i < GameScr.optionBikiep.length; i++) {
                                        if (util.nextInt(1, 10) < 3) {
                                            it.option.add(new Option(GameScr.optionBikiep[i], util.nextInt(GameScr.paramBikiep[i], GameScr.paramBikiep[i] * 70 / 100)));
                                            a++;
                                        }
                                    }
                                    it.setLock(true);
                                    p.nj.addItemBag(true, it);
                                    p.nj.clone.removeItemBody((byte) 15);
                                    p.upluongMessage(-500);
                                    String b = "";
                                    if (a > 5) {
                                        b = "Khá mạnh đó, ngươi thấy ta làm tốt không ?";
                                    } else if (a > 2) {
                                        b = "Không tệ, ngươi xem có ổn không ?";
                                    } else {
                                        b = "Ta chỉ giúp được cho ngươi đến thế thôi. Ta xin lỗi !";
                                    }
                                    p.nj.getPlace().chatNPC(p, (short) 40, b);
                                    break;
                                }
                                case 1: {
                                    if (p.nj.isHuman) {
                                        p.session.sendMessageLog("Chủ thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    if (p.nj.clone.ItemBody[15] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng bí kíp ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                                        return;
                                    }
                                    if (p.nj.getAvailableBag() <= 6) {
                                p.session.sendMessageLog("ít nhất dư 6 ô trong hành trang mới sử dụng được tính năng này");
                                break;
                            }
                                    Item it = p.nj.clone.ItemBody[15];
                                    if (p.nj.clone.nclass == 1 || p.nj.clone.nclass == 2) {
                                        Service.startYesNoDlg(p, (byte) 5, "Con có muốn nâng cấp bí kíp với " + GameScr.luongBikiep[it.getUpgrade()] + " lượng + " + GameScr.daBikiep[it.getUpgrade()] + " đá hỏa và tỷ lệ thành công là: " + GameScr.percentBikiep[it.getUpgrade()] + "% hay không?");
                                    } else if (p.nj.clone.nclass == 3 || p.nj.clone.nclass == 4) {
                                        Service.startYesNoDlg(p, (byte) 5, "Con có muốn nâng cấp bí kíp với " + GameScr.luongBikiep[it.getUpgrade()] + " lượng + " + GameScr.daBikiep[it.getUpgrade()] + "  đá băng và tỷ lệ thành công là: " + GameScr.percentBikiep[it.getUpgrade()] + "% hay không?");
                                    } else if (p.nj.clone.nclass == 5 || p.nj.clone.nclass == 6) {
                                        Service.startYesNoDlg(p, (byte) 5, "Con có muốn nâng cấp bí kíp với " + GameScr.luongBikiep[it.getUpgrade()] + " lượng + " + GameScr.daBikiep[it.getUpgrade()] + " đá gió và tỷ lệ thành công là: " + GameScr.percentBikiep[it.getUpgrade()] + "% hay không?");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                        case 2: {
                            this.server.manager.sendTB(p, "Hướng Dẫn Nâng Bí Kíp", "1. Để nhận bí kíp,các bạn có thể câu cá hoặc ăn sự kiện \n2. Luyện bí kíp giúp chỉ số ngon hơn sẽ mất 500 lượng\n3. Nâng cấp bí kíp có 16 cấp độ \n\n Lưu ý : \n - Nếu chỉ số kém,hãy luyện lại để nhận chỉ số mới\n - Nếu bí kíp đã nâng cấp mà ấn luyện sẽ về +0 ra chỉ số mới \n - Bí kíp +12 có thể dịch chuyển và tinh luyện tăng chỉ số tiếp");
                            break;
                        }
                    }
                    break;
                }
                //NPC Cây Mai
//                case 38:
//                    if (menuId == 0) {
//                        if (p.nj.getAvailableBag() == 0) {
//                            p.sendYellowMessage("Hành trang không đủ chỗ trống");
//                            return;
//                        } else if (p.nj.getLevel() < 10) {
//                            p.sendYellowMessage("Yêu cầu trình độ đạt cấp 10");
//                            return;
//                        } else if (p.nj.quantityItemyTotal(646) <= 0) {
//                            p.sendYellowMessage("Bạn không có bùa có may mắn để tham gia");
//                            return;
//                        } else {
//                            Item itemup;
//                            int henxui = util.nextInt(1000);
//                            if (henxui < 100) {
//                                p.nj.topSK2 += 1;
//                                p.updateExp(500000L, true);
//                                short[] arId = new short[]{9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 281, 383, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453, 337, 338, 567, 477, 477, 684, 684, 788, 788, 789, 789, 778, 778, 778, 778, 778, 778, 778, 541, 542, 852, 852};
//                                short idI = arId[util.nextInt(arId.length)];
//                                itemup = ItemData.itemDefault(idI);
//                                itemup.isExpires = true;
//                                itemup.expires = util.TimeDay(7);
//                                p.nj.addItemBag(true, itemup);
//                                if (idI == 383) {
//                                    server.manager.chatKTG(p.nj.name + " tham gia may mắn đầu xuân nhận được Bát bảo");
//                                }
//                                if (idI == 852) {
//                                    server.manager.chatKTG(p.nj.name + " tham gia may mắn đầu xuân nhận được Pet ứng long");
//                                }
//                                p.nj.removeItemBags(646, 1);
//                                break;
//                            } else {
//                                p.nj.topSK2 += 1;
//                                p.updateExp(500000L, true);
//                                short[] arId = new short[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453, 337, 338, 567, 477, 684, 684, 788, 788, 789, 789, 778, 778, 778, 778, 778, 778, 778, 541, 542};
//                                short idI = arId[util.nextInt(arId.length)];
//                                itemup = ItemData.itemDefault(idI);
//                                itemup.isLock = true;
//                                itemup.expires = util.TimeDay(7);
//                                itemup.isExpires = true;
//                                p.nj.addItemBag(true, itemup);
//                                if (idI == 383) {
//                                    server.manager.chatKTG(p.nj.name + " tham gia may mắn đầu xuân nhận được Bát bảo");
//                                }
//                                if (idI == 852) {
//                                    server.manager.chatKTG(p.nj.name + " tham gia may mắn đầu xuân nhận được Pet ứng long");
//                                }
//                                p.nj.removeItemBags(646, 1);
//                                break;
//                            }
//                        }
//                    }
//                    if (menuId == 1) {
//                        if (p.nj.getAvailableBag() == 0) {
//                            p.sendYellowMessage("Hành trang không đủ chỗ trống");
//                            return;
//                        } else if (p.nj.getLevel() < 40) {
//                            p.sendYellowMessage("Yêu cầu trình độ đạt cấp 40");
//                            return;
//                        } else if (p.nj.hailoc <= 0) {
//                            p.sendYellowMessage("Lượt hái lộc hôm nay của con đã hết");
//                            return;
//                        } else {
//                            Item itemup;
//                            int henxui = util.nextInt(1000);
//                            if (henxui < 100) {
//                                p.updateExp(500000L, true);
//                                short[] arId = new short[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 383, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453, 337, 338, 567, 477, 477, 684, 684, 788, 788, 789, 789, 778, 778, 778, 778, 778, 778, 778, 541, 542, 852};
//                                short idI = arId[util.nextInt(arId.length)];
//                                itemup = ItemData.itemDefault(idI);
//                                itemup.isExpires = true;
//                                itemup.expires = util.TimeDay(7);
//                                p.nj.addItemBag(true, itemup);
//                                if (idI == 383) {
//                                    server.manager.chatKTG(p.nj.name + " hái lộc may mắn nhận được Bát bảo");
//                                }
//                                if (idI == 852) {
//                                    server.manager.chatKTG(p.nj.name + " hái lộc may mắn nhận được Pet ứng long");
//                                }
//                                p.nj.hailoc--;
//                                break;
//                            } else {
//                                p.updateExp(500000L, true);
//                                short[] arId = new short[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453, 337, 338, 567, 477, 684, 684, 788, 788, 789, 789, 778, 778, 778, 778, 778, 778, 778, 541, 542, 852};
//                                short idI = arId[util.nextInt(arId.length)];
//                                itemup = ItemData.itemDefault(idI);
//                                itemup.isLock = true;
//                                itemup.expires = util.TimeDay(7);
//                                itemup.isExpires = true;
//                                p.nj.addItemBag(true, itemup);
//                                if (idI == 383) {
//                                    server.manager.chatKTG(p.nj.name + " hái lộc may mắn nhận được Bát bảo");
//                                }
//                                if (idI == 852) {
//                                    server.manager.chatKTG(p.nj.name + " hái lộc may mắn nhận được Pet ứng long");
//                                }
//                                p.nj.hailoc--;
//                                break;
//                            }
//                        }
//                    }
//                    if (menuId == 2) {
//                        server.manager.sendTB(p, "Hướng dẫn", "- Trong dịp Tết nguyên đán này sẽ có Cây Mai tại làng TONE và 3 Trường, Các bạn có thể chọn Hái lộc để nhận lấy những món quà xuân đầu năm\n\n- Mỗi ngày đăng nhập sẽ nhận được 2 lượt Hái lộc Free\n\n"
//                                + "- Ngoài ra các bạn còn có thể sử dụng Bùa may mắn (NPC Goosho) để tham gia hoạt động May mắn đầu xuân tại Cây Mai.");
//                        break;
//                    }
//                    break;
                case 18: {
                    int num = util.nextInt(0, 2);

                    switch (num) {
                        case 0:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Làng ta sống chủ yếu là nghề biển");
                            break;
                        case 1:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Sống ở làng Chài thì con cần học cách đánh bắt cá.");
                            break;
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đây là làng Chài, do ta quản lý.");
                            break;
                    }
                }
                break;
                case 19: {
                    int num = util.nextInt(0, 2);

                    switch (num) {
                        case 0:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Làng ta khí hậu ôn hòa cây cối quanh năm tươi tốt");
                            break;
                        case 1:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Dân làng sống ra hòa thuận, mọi người rất yêu hòa bình.");
                            break;
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta là Kirin, ngôi làng này do ta cai quản.");
                            break;
                    }
                }
                break;
                case 21: {
                    int num = util.nextInt(0, 2);

                    switch (num) {
                        case 0:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Khí hậu làng ta rất lạnh, sống ở đây phải chăm chỉ rèn luyện");
                            break;
                        case 1:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con thích săn bắt không? Ta rất thích đi săn bắt");
                            break;
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta là Sunoo, ngôi làng này do ta cai quản.");
                            break;
                    }
                }
                break;
                case 22: {
                    switch (menuId) {
                        case 0: {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức Năng Ninja Đệ Nhất sẽ cố gắng ra mắt sớm nhất");
                            break;
                        }
                    }
                }
                break;
                case 36: {
                    p.nj.getPlace().chatNPC(p, (short) npcId, "");
                }
                break;
//                case 36: {
//                    switch (menuId) {                        
//                        case 0: {
//                            if (p.nj.quantityItemyTotal(788) < 15000) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 15000 nham thạch");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(786);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(788, 15000);
//                                break;
//                            }
//                        }
//                        case 1: {
//                            if (p.nj.quantityItemyTotal(789) < 15000) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 15000 pha lê");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(787);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(789, 15000);
//                                break;
//                            }
//                        }
//                        case 2: {
//                            if (p.nj.quantityItemyTotal(682) < 35000) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 35000 đá mặt trăng");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(797);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(682, 35000);
//                                break;
//                            }
//                        }
//                    }
//                }
//                break;
//                case 42: {
//                    switch (menuId) {
//                        case 0: {
//                            switch (optionId) {
//                                case 0: {
//                                    if (p.nj.isNhanban) {
//                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
//                                        return;
//                                    }
//                                    if (p.nj.getLevel() < 90) {
//                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Level 90 mới chơi được con nhé !");
//                                        break;
//                                    }
//                                    p.typemenu = ((optionId == 0) ? 157 : 158);
//                                    doMenuArray(p, new String[]{"Tài xỉu", "Xóc đĩa", "Nổ hũ"});
//                                    break;
//                                }
//                            }
//                        }
//                        break;
//                        case 1: {
//                            if (p.nj.isNhanban) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
//                                return;
//                            }
//                            switch (optionId) {
//                                case 0: {
//                                    if (p.luong < 5000) {
//                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn không có đủ 5000 lượng");
//                                        break;
//                                    }
//                                    if (p.nj.kichhoatkm == 1) {
//                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn đã đăng ký rồi");
//                                    } else {
//                                        if (p.nj.isNhanban) {
//                                            p.session.sendMessageLog("Chức năng này không dành cho phân thân");
//                                        } else {
//                                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đăng ký kinh mạch thành công");
//                                            p.nj.kichhoatkm = 1;
//                                            p.upluongMessage(-5000);
//                                        }
//                                    }
//                                }
//                                break;
//                                case 1: {
//                                    if (p.nj.kichhoatkm != 1) {
//                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đăng ký !");
//                                        break;
//                                    }
//                                    p.typemenu = ((menuId == 1) ? 4445 : 4445);
//                                    doMenuArray(p, new String[]{"Thông tin kinh mạch", "Exp kinh mạch đang có"});
//                                    break;
//                                }
//                                case 2: {
//                                    if (p.nj.kichhoatkm != 1) {
//                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đăng ký !");
//                                        break;
//                                    }
//                                    p.typemenu = ((menuId == 2) ? 4444 : 4444);
//                                    doMenuArray(p, new String[]{" Khai mở kinh mạch", "Nâng kinh mạch cấp 2", "Nâng kinh mạch cấp 3", "Nâng kinh mạch cấp 4", "Nâng kinh mạch cấp 5", "Nâng kinh mạch cấp 6", "Nâng kinh mạch cấp 7", "Nâng kinh mạch cấp 8", "Nâng kinh mạch cấp 9", "Hướng dẫn"});
//                                    break;
//                                }
//                                case 3: {
//                                    this.server.manager.sendTB(p, "Hướng Dẫn", "-Để tham gia kinh mạch con cần phải đăng kí \n\n-Con cần 5000 lượng để đăng ký nhé");
//                                }
//                            }
//                            break;
//                        }
//                    }
//                }
//                break;
                case 157: {
                    switch (menuId) {
                        case 0: {
                            p.typemenu = ((menuId == 0) ? 159 : 160);
                            doMenuArray(p, new String[]{"1k Lượng", "5k Lượng", "10k Lượng"});
                            break;
                        }
                        case 1: {
                            p.typemenu = ((menuId == 1) ? 161 : 162);
                            doMenuArray(p, new String[]{"1k Lượng", "5k Lượng", "10k Lượng"});
                            break;
                        }
                        case 2: {
                            p.typemenu = ((menuId == 2) ? 210 : 211);
                            doMenuArray(p, new String[]{"Hũ 10k", "Hũ 20k", "Hũ 50k", "Hướng dẫn"});
                            break;
                        }
                    }
                    break;
                }
                case 210: {
                    switch (menuId) {
                        case 0: {
                            Player u = null;
                            for (short i = 0; i < this.player10k.size(); ++i) {
                                if (this.player10k.get(i).name.equals(p.nj.name)) {
                                    u = this.player10k.get(i);
                                    break;
                                }
                            }
                            if (p.nj.xu < 10000) {
                                p.sendYellowMessage("Bạn không đủ xu tham gia");
                                return;
                            } else {
                                u = new Player(p.id);
                                p.nj.upXuMessage(-10000);
                                //  u.joinAmount = util.nextInt(0, 10000);
                                u.joinXu = "9000";
                                u.joinAmount = Integer.parseInt(u.joinXu);
                                this.xuwin10k += util.nextInt(5000, u.joinAmount);
                                this.xuhu10k = String.format("%,d", this.xuwin10k);
                                u.user = p.username;
                                u.name = p.nj.name;
                                this.player10k.add(u);
                                this.xuclone10k += this.xuwin10k;
                                if (this.xuhu10k.contains("888") || this.xuhu10k.contains("777") || this.xuhu10k.contains("666") || this.xuhu10k.contains("555") || this.xuhu10k.contains("444") || this.xuhu10k.contains("333")
                                        && this.xuhu10k.contains("333") || this.xuhu10k.contains("222") || this.xuhu10k.contains("111") || this.xuhu10k.contains("999") || this.xuhu10k.contains("000")) {
                                    this.getJoinAmount10k(u.name);
                                    p.nj.upxuMessage(xuwin10k);
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chúc mừng con " + u.name + " đã chiến thắng " + String.format("%,d", xuwin10k) + " xu");
                                    Manager.chatKTG("Chúc mừng " + u.name + " đã nổ hũ, số xu nhận được là " + String.format("%,d", xuwin10k) + " xu");
                                    this.player10k.clear();
                                    this.xuwin10k = 0;
                                    break;
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Số xu trong hũ đang là " + String.format("%,d", xuwin10k) + " xu thử lại đi con");
                                    break;
                                }
                            }
                        }
                        case 1: {
                            Player u = null;
                            for (short i = 0; i < this.player20k.size(); ++i) {
                                if (this.player20k.get(i).name.equals(p.nj.name)) {
                                    u = this.player20k.get(i);
                                    break;
                                }
                            }
                            if (p.nj.xu < 20000) {
                                p.sendYellowMessage("Bạn không đủ xu tham gia");
                                return;
                            } else {
                                u = new Player(p.id);
                                p.nj.upXuMessage(-20000);
                                //  u.joinAmount = util.nextInt(0, 10000);
                                u.joinXu = "18000";
                                u.joinAmount = Integer.parseInt(u.joinXu);
                                this.xuwin20k += util.nextInt(10000, u.joinAmount);
                                this.xuhu20k = String.format("%,d", this.xuwin20k);
                                u.user = p.username;
                                u.name = p.nj.name;
                                this.player20k.add(u);
                                this.xuclone20k += this.xuwin20k;
                                if (this.xuhu20k.contains("888") || this.xuhu20k.contains("777") || this.xuhu20k.contains("666") || this.xuhu20k.contains("555") || this.xuhu20k.contains("444") || this.xuhu20k.contains("333")
                                        && this.xuhu20k.contains("333") || this.xuhu20k.contains("222") || this.xuhu20k.contains("111") || this.xuhu20k.contains("999") || this.xuhu20k.contains("000")) {
                                    this.getJoinAmount20k(u.name);
                                    p.nj.upxuMessage(xuwin20k);
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chúc mừng con " + u.name + " đã chiến thắng " + String.format("%,d", xuwin20k) + " xu");
                                    Manager.chatKTG("Chúc mừng " + u.name + " đã nổ hũ, số xu nhận được là " + String.format("%,d", xuwin20k) + " xu");
                                    this.player20k.clear();
                                    this.xuwin20k = 0;
                                    break;
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Số xu trong hũ đang là " + String.format("%,d", xuwin20k) + " xu thử lại đi con");
                                    break;
                                }
                            }
                        }
                        case 2: {
                            Player u = null;
                            for (short i = 0; i < this.player50k.size(); ++i) {
                                if (this.player50k.get(i).name.equals(p.nj.name)) {
                                    u = this.player50k.get(i);
                                    break;
                                }
                            }
                            if (p.nj.xu < 50000) {
                                p.sendYellowMessage("Bạn không đủ xu tham gia");
                                return;
                            } else {
                                u = new Player(p.id);
                                p.nj.upXuMessage(-50000);
                                //  u.joinAmount = util.nextInt(0, 10000);
                                u.joinXu = "48000";
                                u.joinAmount = Integer.parseInt(u.joinXu);
                                this.xuwin50k += util.nextInt(30000, u.joinAmount);
                                this.xuhu50k = String.format("%,d", this.xuwin50k);
                                u.user = p.username;
                                u.name = p.nj.name;
                                this.player50k.add(u);
                                this.xuclone50k += this.xuwin50k;
                                if (this.xuhu50k.contains("888") || this.xuhu50k.contains("777") || this.xuhu50k.contains("666") || this.xuhu50k.contains("555") || this.xuhu50k.contains("444") || this.xuhu50k.contains("333")
                                        && this.xuhu50k.contains("333") || this.xuhu50k.contains("222") || this.xuhu50k.contains("111") || this.xuhu50k.contains("999") || this.xuhu50k.contains("000")) {
                                    this.getJoinAmount50k(u.name);
                                    p.nj.upxuMessage(xuwin50k);
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chúc mừng con " + u.name + " đã chiến thắng " + String.format("%,d", xuwin50k) + " xu");
                                    Manager.chatKTG("Chúc mừng " + u.name + " đã nổ hũ, số xu nhận được là " + String.format("%,d", xuwin50k) + " xu");
//                       if (p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 0) {
//                       p.nj.taskDanhVong[1]++;
//                       }
//                       if(p.nj.taskDanhVong[1] == p.nj.taskDanhVong[2]) {
//                       p.sendYellowMessage("Bạn đã hoàn thành nhiệm vụ danh vọng.");
//                       }
                                    this.player50k.clear();
                                    xuwin50k = 0;
                                    break;
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Số xu trong hũ đang là " + String.format("%,d", xuwin50k) + " xu thử lại đi con");
                                    break;
                                }
                            }
                        }
                        case 3: {
                            server.manager.sendTB(p, "Luật chơi", "- Số xu trong hũ chỉ cần có 3 con số sau  000 -> 999 bạn là người nhận được toàn bộ số xu trong hũ");
                            break;
                        }
                    }
                    break;
                }
                case 161: {
                    switch (menuId) {
                        case 0: {
                            p.typemenu = ((menuId == 0) ? 200 : 201);
                            doMenuArray(p, new String[]{"Chẵn", "Lẻ"});
                            break;
                        }
                        case 1: {
                            p.typemenu = ((menuId == 1) ? 202 : 203);
                            doMenuArray(p, new String[]{"Chẵn", "Lẻ"});
                            break;
                        }
                        case 2: {
                            p.typemenu = ((menuId == 2) ? 204 : 205);
                            doMenuArray(p, new String[]{"Chẵn", "Lẻ"});
                            break;
                        }
                    }
                    break;
                }
                case 159: {
                    switch (menuId) {
                        case 0: {
                            p.typemenu = ((menuId == 0) ? 163 : 164);
                            doMenuArray(p, new String[]{"Tài", "Xỉu"});
                            break;
                        }
                        case 1: {
                            p.typemenu = ((menuId == 1) ? 165 : 166);
                            doMenuArray(p, new String[]{"Tài", "Xỉu"});
                            break;
                        }
                        case 2: {
                            p.typemenu = ((menuId == 2) ? 167 : 168);
                            doMenuArray(p, new String[]{"Tài", "Xỉu"});
                            break;
                        }
                    }
                    break;
                }
                case 163:
                    /* 1317 */ switch (menuId) {
                        /*      */ case 0: {
                            /* 1319 */ if (p.luong > 1000) {
                                /* 1320 */ p.upluongMessage(-1000L);
                                /* 1321 */ int x = util.nextInt(5) + 1;
                                int x1 = util.nextInt(5) + 1;
                                int x2 = util.nextInt(5) + 1;
                                int ketqua = x + x1 + x2;
                                /* 1322 */ if (ketqua >= 11 && ketqua <= 18) {
                                    /* 1323 */ p.upluongMessage(1800L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Tài thắng rồi !");


                                    //       Manager.chatKTG(p.nj.name + " đặt tài chiến thắng 10.000 lượng");
/*      */ break;
                                    /*      */                                } /* 1327 */ else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Xỉu còn cái nịt !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                        /*      */ case 1: {
                            /* 1319 */ if (p.luong > 1000) {
                                /* 1320 */ p.upluongMessage(-1000L);
                                /* 1321 */ int x = util.nextInt(6) + 1;
                                int x1 = util.nextInt(6) + 1;
                                int x2 = util.nextInt(6) + 1;
                                int ketqua = x + x1 + x2;
                                /* 1322 */ if (ketqua >= 3 && ketqua <= 10) {
                                    /* 1323 */ p.upluongMessage(1800L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Xỉu thắng rồi !");


                                    //       Manager.chatKTG(p.nj.name + " đặt xỉu chiến thắng 98.000 lượng");
/*      */ break;
                                    /*      */                                } else {
                                    /* 1327 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Tài còn cái nịt !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                    }
                    break;
                case 165:
                    /* 1317 */ switch (menuId) {
                        /*      */ case 0: {
                            /* 1319 */ if (p.luong > 5000) {
                                /* 1320 */ p.upluongMessage(-5000L);
                                /* 1321 */ int x = util.nextInt(5) + 1;
                                int x1 = util.nextInt(5) + 1;
                                int x2 = util.nextInt(5) + 1;
                                int ketqua = x + x1 + x2;
                                /* 1322 */ if (ketqua >= 11 && ketqua <= 18) {
                                    /* 1323 */ p.upluongMessage(9000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Tài thắng rồi !");


                                    //        Manager.chatKTG(p.nj.name + " đặt tài chiến thắng 98.000 lượng");
/*      */ break;
                                    /*      */                                } /* 1327 */ else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Xỉu còn cái nịt !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lương !");
                                /*      */ break;
                            }
                        }
                        /*      */ case 1: {
                            /* 1319 */ if (p.luong > 5000) {
                                /* 1320 */ p.upluongMessage(-5000L);
                                /* 1321 */ int x = util.nextInt(6) + 1;
                                int x1 = util.nextInt(6) + 1;
                                int x2 = util.nextInt(6) + 1;
                                int ketqua = x + x1 + x2;
                                /* 1322 */ if (ketqua >= 3 && ketqua <= 10) {
                                    /* 1323 */ p.upluongMessage(9000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Xỉu thắng rồi !");


                                    //       Manager.chatKTG(p.nj.name + " đặt xỉu chiến thắng 98.000 lượng");
/*      */ break;
                                    /*      */                                } else {
                                    /* 1327 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Tài còn cái nịt !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                    }
                    break;
                case 167:
                    /* 1317 */ switch (menuId) {
                        /*      */ case 0: {
                            /* 1319 */ if (p.luong > 10000) {
                                /* 1320 */ p.upluongMessage(-10000L);
                                /* 1321 */ int x = util.nextInt(5) + 1;
                                int x1 = util.nextInt(5) + 1;
                                int x2 = util.nextInt(5) + 1;
                                int ketqua = x + x1 + x2;
                                /* 1322 */ if (ketqua >= 11 && ketqua <= 18) {
                                    /* 1323 */ p.upluongMessage(18000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Tài thắng rồi !");
                                    Manager.chatKTG(p.nj.name + " đặt Tài chiến thắng 18.000 lượng");
                                    /*      */ break;
                                    /*      */                                } /* 1327 */ else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Xỉu còn cái nịt !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                        /*      */ case 1: {
                            /* 1319 */ if (p.luong > 10000) {
                                /* 1320 */ p.upluongMessage(-10000L);
                                /* 1321 */ int x = util.nextInt(6) + 1;
                                int x1 = util.nextInt(6) + 1;
                                int x2 = util.nextInt(6) + 1;
                                int ketqua = x + x1 + x2;
                                /* 1322 */ if (ketqua >= 3 && ketqua <= 10) {
                                    /* 1323 */ p.upluongMessage(18000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Xỉu thắng rồi !");
                                    Manager.chatKTG(p.nj.name + " đặt Xỉu chiến thắng 18.000 lượng");
                                    /*      */ break;
                                    /*      */                                } else {
                                    /* 1327 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Kết quả là " + x + " " + x1 + " " + x2 + " = " + ketqua + " điểm Tài còn cái nịt !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                    }
                    break;
                case 200:
                    /* 1317 */ switch (menuId) {
                        /*      */ case 0: {
                            /* 1319 */ if (p.luong > 1000) {
                                /* 1320 */ p.upluongMessage(-1000L);
                                /* 1321 */ int x = util.nextInt(2);
                                /* 1322 */ if (x == 0) {
                                    /* 1323 */ p.upluongMessage(1800L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, " Chẵn Thắng rồi !");


                                    //        Manager.chatKTG(p.nj.name + " đặt chẵn chiến thắng 19.000 lượng");
/*      */ break;
                                    /*      */                                } /* 1327 */ else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Thất bại là mẹ thành công !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                        /*      */ case 1: {
                            /* 1319 */ if (p.luong > 1000) {
                                /* 1320 */ p.upluongMessage(-1000);
                                /* 1321 */ int x = util.nextInt(2);
                                /* 1322 */ if (x == 1) {
                                    /* 1323 */ p.upluongMessage(1800L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, " Lẻ Thắng rồi !");


                                    //        Manager.chatKTG(p.nj.name + " đặt lẻ chiến thắng 19.000 lượng");
/*      */ break;
                                    /*      */                                } else {
                                    /* 1327 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Thất bại là mẹ thành công !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                    }
                    break;
                case 202:
                    /* 1317 */ switch (menuId) {
                        /*      */ case 0: {
                            /* 1319 */ if (p.luong > 5000) {
                                /* 1320 */ p.upluongMessage(-5000L);
                                /* 1321 */ int x = util.nextInt(2);
                                /* 1322 */ if (x == 0) {
                                    /* 1323 */ p.upluongMessage(9000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, " Chẵn Thắng rồi !");


                                    //          Manager.chatKTG(p.nj.name + " đặt chẵn chiến thắng 98.000 lượng");
/*      */ break;
                                    /*      */                                } /* 1327 */ else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Thất bại là mẹ thành công !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                        /*      */ case 1: {
                            /* 1319 */ if (p.luong > 5000) {
                                /* 1320 */ p.upluongMessage(-5000);
                                /* 1321 */ int x = util.nextInt(2);
                                /* 1322 */ if (x == 1) {
                                    /* 1323 */ p.upluongMessage(9000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, " Lẻ Thắng rồi !");


                                    //         Manager.chatKTG(p.nj.name + " đặt lẻ chiến thắng 98.000 lượng");
/*      */ break;
                                    /*      */                                } else {
                                    /* 1327 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Thất bại là mẹ thành công !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                    }
                    break;
                case 204:
                    /* 1317 */ switch (menuId) {
                        /*      */ case 0: {
                            /* 1319 */ if (p.luong > 10000) {
                                /* 1320 */ p.upluongMessage(-10000L);
                                /* 1321 */ int x = util.nextInt(2);
                                /* 1322 */ if (x == 0) {
                                    /* 1323 */ p.upluongMessage(18000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, " Chẵn Thắng rồi !");
                                    Manager.chatKTG(p.nj.name + " đặt Chẵn chiến thắng 18.000 lượng");
                                    /*      */ break;
                                    /*      */                                } /* 1327 */ else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Thất bại là mẹ thành công !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                        /*      */ case 1: {
                            /* 1319 */ if (p.luong > 10000) {
                                /* 1320 */ p.upluongMessage(-10000);
                                /* 1321 */ int x = util.nextInt(2);
                                /* 1322 */ if (x == 1) {
                                    /* 1323 */ p.upluongMessage(18000L);
                                    /* 1324 */ p.nj.getPlace().chatNPC(p, (short) npcId, " Lẻ Thắng rồi !");
                                    Manager.chatKTG(p.nj.name + " đặt Lẻ chiến thắng 18.000 lượng");
                                    /*      */ break;
                                    /*      */                                } else {
                                    /* 1327 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Thất bại là mẹ thành công !");
                                    /*      */ break;
                                }
                                /*      */                            } else {
                                /* 1330 */ p.nj.getPlace().chatNPC(p, (short) npcId, "Con không đủ lượng !");
                                /*      */ break;
                            }
                        }
                    }
                    break;
                case 41: {
                    switch (menuId) {
                        case 0: {
                            if (p.diemdanh) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hôm nay con đã điểm danh rồi nhé, hãy quay lại đây vào ngày mai");
                                break;
                            }
                            p.diemdanh = true;
                            p.upluongMessage(500);
                            p.nj.upyenMessage(20000000);
                            p.nj.upxuMessage(1000000);
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Điểm danh mỗi ngày sẽ nhận được các phần quà giá trị");
                            break;
                        }
                        case 1: {
                            if (p.tanthu) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Mỗi tài khoản chỉ có thể nhận quà tân thủ 1 lần");
                            } else {
                                if (p.nj.isNhanban) {
                                    p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Nhận quà tân thủ thành công, ngươi hãy sử dụng chúng một cách hợp lí nhé");
                                    p.tanthu = true;
                                    p.nj.upyenMessage(200000000);
                                    p.nj.upxuMessage(10000000);
                                    p.upluongMessage(15000);
                                }
                            }
                        }
                        break;
                        case 2: {
                            p.passold = "";
                            this.sendWrite(p, (short) 51, "Nhập mật khẩu cũ");
                            break;
                        }
                        case 3: {
                            if (p.nj.exptype == 0) {
                                p.nj.exptype = 1;
                                p.session.sendMessageLog("Đã tắt không nhận kinh nghiệm");
                                break;
                            }
                            p.nj.exptype = 0;
                            p.session.sendMessageLog("Đã bật không nhận kinh nghiệm");
                            break;
                        }
                        case 4: {
                            this.sendWrite(p, (short) 49, "Mã quà tặng:");
                            break;
                        }                       
                    }
                }
                break;
                // nâng cấp pet sự kiện
                case 35: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.ItemBody[26] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet bóng ma để có thể hoán đổi.");
                                break;
                            }
                            if (p.nj.ItemBody[26].id != 825) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet bóng ma để có thể hoán đổi.");
                                break;
                            }
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                break;
                            }
                            if (p.nj.getAvailableBag() <= 6) {
                                p.session.sendMessageLog("ít nhất dư 6 ô trong hành trang mới sử dụng được tính năng này");
                                break;
                            }
                            if (p.luong < 500) {
                                p.session.sendMessageLog("Bạn không có đủ 500 lượng");
                                break;
                            }
                            final Item it = ItemData.itemDefault(826);
                            int a = 0;
                            for (int i = 0; i < GameScr.optionPet.length; i++) {
                                if (util.nextInt(1, 10) < 3) {
                                    it.option.add(new Option(GameScr.optionPet[i], util.nextInt(GameScr.paramPet[i], GameScr.paramPet[i] * 70 / 100)));
                                    a++;
                                }
                            }
                            it.setLock(true);
                            p.nj.addItemBag(true, it);
                            p.nj.removeItemBody((byte) 26);
                            p.upluongMessage(-500);
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hoán đổi thành công.");
                            break;
                        }
                        case 1: {
                            if (p.nj.ItemBody[26] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet yêu tinh mới hủy được.");
                                break;
                            }
                            if (p.nj.ItemBody[26].id != 826) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet yêu tinh mới hủy được.");
                                break;
                            }
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                break;
                            }
                            p.nj.removeItemBody((byte) 26);
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn đã hủy pet thành công");
                            break;
                        }
                        case 2: {
                            this.server.manager.sendTB(p, "Hướng Dẫn Nâng Pet", "1. Đây là npc pet trang bị 2\n\n"
                                    + "2. Pet bóng ma ăn sự kiện ngẫu nhiên có\n\n"
                                    + "3. Phí tô màu pet + random chỉ số là 500 lượng 1 lượt");
                            break;
                        }
                    }
                    break;
                }
                //ấn tộc npc
                case 44: {
                    switch (menuId) {

                        case 0: {
                            p.session.sendMessageLog("Bạn đang có " + p.nj.pointTinhTu + " điểm ấn tộc");
                        }
                        break;
                        case 1: {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.pointTinhTu >= 300) {
                                if (p.nj.getAvailableBag() < 6) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không đủ 6 ô chỗ trống để nhận ấn tộc");
                                    return;
                                }

                                Item item = ItemData.itemDefault(1011, true);
                                item.quantity = 1;
                                item.upgrade = 1;
                                item.isLock = true;
                                Option op = new Option(6, 1000);
                                item.option.add(op);
                                op = new Option(118, 50);
                                item.option.add(op);
                                p.nj.pointTinhTu -= 300;
                                p.nj.addItemBag(true, item);
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ 300 điểm để nhận ấn tộc");
                            }
                        }
                        break;
                        case 2: {
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.pointTinhTu >= 300) {
                                if (p.nj.getAvailableBag() < 6) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không đủ 6 ô trống để nhận giấy thăng ấn");
                                    return;
                                }
                                Item item = ItemData.itemDefault(1025);
                                item.quantity = 1;
                                p.nj.pointTinhTu -= 300;
                                p.nj.addItemBag(true, item);
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ 300 điểm để nhận giấy phép thăng ấn");
                            }
                        }
                        break;
                        case 3: {
                            if (p.nj.getAvailableBag() <= 6) {
                                p.session.sendMessageLog("ít nhất dư 6 ô trong hành trang mới sử dụng được tính năng này");
                                break;
                            }
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                return;
                            }

                            if (p.nj.ItemBody[29] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy đeo ấn vào người trước rồi nâng cấp nhé.");
                                return;
                            }

                            if (p.nj.ItemBody[29] == null) {
                                return;
                            }

                            if (p.nj.ItemBody[29].upgrade >= 10) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ấn tộc của con đã đạt cấp tối đa");
                                return;
                            }

                            ItemData data = ItemData.ItemDataId(p.nj.ItemBody[29].id);
                            Service.startYesNoDlg(p, (byte) 6, "Bạn có muốn nâng cấp " + data.name + " với " + GameScr.coinUpAn[p.nj.ItemBody[29].upgrade] + " yên hoặc xu và " + GameScr.goldUpAn[p.nj.ItemBody[29].upgrade] + " lượng với tỷ lệ thành công là " + GameScr.percentUpAn[p.nj.ItemBody[29].upgrade] + "% không?");
                        }
                        break;
                        case 4: {
                            
                            if (p.nj.ItemBody[29] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng ấn tộc mới hủy được.");
                                break;
                            }
                            if (p.nj.ItemBody[29].id != 1011) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng ấn tộc mới hủy được.");
                                break;
                            }
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                break;
                            }
                            p.nj.removeItemBody((byte) 29);
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn đã hủy ấn tộc thành công");
                            break;
                        }
                        case 5: {
                            this.server.manager.sendTB(p, "Hướng Dẫn", "1. Điểm ấn tộc :\n"
                                    + "- Làm NVHN,tà thú,hang động,đi LDGT sẽ có điểm\n"
                                    + "- 500đ đổi ấn tộc,300đ đổi giấy thăng cấp\n\n"
                                    + "2. Ghép đá linh hồn :\n"
                                    + "- Bạn cần có trượng linh hồn + bụi linh hồn để nâng cấp thành đá\n"
                                    + "- Trượng và bụi,đá linh hồn sẽ có trong sự kiện\n\n"
                                    + "3. Nâng cấp ấn tộc :\n"
                                    + "- Mỗi cấp cần 10 viên đá theo cấp + giấy thăng cấp\n\n"
                                    + "4. Hiệu ứng hào quang\n"
                                    + "- Cấp +6,8,10 sẽ có 3 hào quang theo cấp\n"
                                    + "- Hiệu ứng hào quang ấn tộc sẽ riêng không chung hào quang khác");
                            break;
                        }
                    }
                }
                break;
                case 25: {
                    switch (menuId) {
                        case 0: {
                            p.nj.getPlace().chatNPC(p, (short) 25, "Con có thể nhận được một chút ngân lượng nếu giúp ta làm nhiệm vụ");
                            break;
                        }
                        case 1: {
                            switch (optionId) {
                                case 0: {
                                    // Nhiem vu hang ngay
                                    if (p.nj.getTasks()[NHIEM_VU_HANG_NGAY] == null && p.nj.nvhnCount < 20) {
                                        val task = createTask(p.nj.getLevel());
                                        if (task != null) {
                                            p.nj.addTaskOrder(task);
                                        } else {
                                            p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này có chút trục trặc chắc con không làm được rồi ahihi");
                                        }
                                    } else if (p.nj.nvhnCount >= 20) {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ hôm nay con đã làm hết quay lại vào ngày hôm sau");
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần trước ta giao cho con vẫn chưa hoàn thành");
                                    }
                                    break;
                                }
                                case 1: {
                                    // Huy nhiem vu
                                    if (p.nj.getTasks() != null && p.nj.getTasks()[NHIEM_VU_HANG_NGAY] != null) {
                                        p.nj.huyNhiemVu(NHIEM_VU_HANG_NGAY);
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Ta đã hủy nhiệm vụ của con. Lần sau cố gắng hoàn thành tốt nhiệm vụ con nhé");
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hiện tại con chưa có nhiệm vụ để hủy.");
                                    }
                                    break;
                                }
                                case 2: {
                                    // Hoan thanh
                                    if (!p.nj.hoanThanhNhiemVu(NHIEM_VU_HANG_NGAY)) {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hãy hoàn thành nhiệm vụ để được nhận thưởng");
                                    } else {
                                        // TODO nhan qua NVHN
//                                        p.upluongMessage(util.nextInt(MIN_YEN_NVHN, MAX_YEN_NVHN));
                                        p.upluongMessage(20);
                                        p.nj.upyenMessage(util.nextInt(MIN_YEN_NVHN * 100, MAX_YEN_NVHN * 200));
                                        ++p.nj.pointUydanh;
                                        ++p.nj.pointTinhTu;
                                        if (util.nextInt(100) < 100) {  // tỉ lệ rơi đá linh hồn
                                            Item it = ItemData.itemDefault(1001);
                                            it.quantity = 1;
                                            it.isLock = true;
                                            p.nj.addItemBag(true, it);
                                        }
                                        p.sendYellowMessage("Bạn nhận được 1 điểm ấn tộc");
                                        if ((p.nj.getTaskId() == 30 && p.nj.getTaskIndex() == 1)
                                                || (p.nj.getTaskId() == 39 && p.nj.getTaskIndex() == 3)) {
                                            p.nj.upMainTask();
                                        }
                                    }
                                    break;
                                }

                                case 3: {
                                    // Di toi
                                    if (p.nj.getTasks() != null
                                            && p.nj.getTasks()[NHIEM_VU_HANG_NGAY] != null) {
                                        val task = p.nj.getTasks()[NHIEM_VU_HANG_NGAY];
                                        val map = Server.getMapById(task.getMapId());
                                        p.nj.setMapid(map.id);
                                        for (Npc npc : map.template.npc) {
                                            if (npc.id == 13) {
                                                p.nj.x = npc.x;
                                                p.nj.y = npc.y;
                                                p.nj.getPlace().leave(p);
                                                map.getFreeArea().Enter(p);
                                                break;
                                            }
                                        }
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này gặp lỗi con hãy đi up level lên đi rồi nhận lại nhiệm vụ từ ta");
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hãy nhận nhiệm vụ từ ta để có thể chuyển map");
                                    }
                                }
                            }
                            break;
                        }
                        case 2: {
                            // Ta thu
                            switch (optionId) {
                                case 0: {
                                    //Nhan nhiem vu
                                    if (p.nj.getTasks()[NHIEM_VU_TA_THU] == null) {
                                        if (p.nj.taThuCount > 0) {
                                            val task = createBeastTask(p.nj.getLevel());
                                            if (task != null) {
                                                p.nj.addTaskOrder(task);
                                            } else {
                                                p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ ngày hôm nay đã hêt");
                                            }
                                        } else {
                                            p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ ngày hôm nay đã hêt");
                                        }
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần trước ta giao cho con vẫn chưa hoàn thành");
                                    }
                                    break;
                                }
                                case 1: {
                                    p.nj.huyNhiemVu(NHIEM_VU_TA_THU);
                                    break;
                                }
                                case 2: {
                                    if (!p.nj.hoanThanhNhiemVu(NHIEM_VU_TA_THU)) {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hãy hoàn thành nhiệm vụ để được nhận thưởng");
                                    } else {
                                        val i = ItemData.itemDefault(251);
                                        i.quantity = p.nj.get().getLevel() >= 60 ? 20 : 10;
                                        p.nj.addItemBag(true, i);
                                        ++p.nj.pointUydanh;
                                        p.nj.pointTinhTu += 5;
                                        p.sendYellowMessage("Bạn nhận được 5 điểm ấn tộc");
                                        if (util.nextInt(100) < 100) {  // tỉ lệ rơi đá linh hồn
                                            Item it = ItemData.itemDefault(1001);
                                            it.quantity = 1;
                                            it.isLock = true;
                                            p.nj.addItemBag(true, it);
                                        }
                                        if ((p.nj.getTaskId() == 30 && p.nj.getTaskIndex() == 2) || (p.nj.getTaskId() == 39 && p.nj.getTaskIndex() == 1)) {
                                            //p.upluongMessage(util.nextInt(MIN_YEN_NVHN * 10, MAX_YEN_NVHN * 10));
                                            p.nj.upyenMessage(util.nextInt(MIN_YEN_NVHN * 160, MAX_YEN_NVHN * 300));
                                            p.nj.upMainTask();
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case 3: {
                            // Chien truong
                            switch (optionId) {
                                case 0: {
                                    // bach
                                    p.nj.enterChienTruong(IBattle.CAN_CU_DIA_BACH);
                                    break;
                                }
                                case 1: {
                                    // hac gia
                                    p.nj.enterChienTruong(IBattle.CAN_CU_DIA_HAC);
                                    break;
                                }
                                case 2: {
                                    Service.sendBattleResult(p.nj, Server.getInstance().globalBattle);
                                    break;
                                }
                                case 3: {
                                    if (p.nj.quantityItemyTotal(819) < 10) {
                                        p.sendYellowMessage("Hành trang của con không có đủ 10 đồng xu đỏ");
                                    } else if (p.nj.getAvailableBag() == 0) {
                                        p.sendYellowMessage("Hành trang không đủ chỗ trống");
                                    } else {
                                        Item it = ItemData.itemDefault(389);
                                        p.nj.addItemBag(true, it);
                                        p.nj.removeItemBags(819, 10);
                                    }
                                    break;
                                }
                                case 4: {
                                    this.server.manager.sendTB(p, "Hướng Dẫn", "1. Chiến trường sẽ mở vào các khung giờ 13h và 20h. \n\n2. Ngoài ra vào Chủ Nhật mỗi tuần đánh người sẽ rơi đồng xu đổi lấy quà \n\n 3.Anh em nhớ điểm danh đi vào khoảng time 13h-13h10, 20h-20h10");
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 26: {
                    if (menuId == 0) {
                        p.openUI(14);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(15);
                        break;
                    }
                    if (menuId == 2) {
                        p.openUI(32);
                        break;
                    }
                    if (menuId == 3) {
                        p.openUI(34);
                        break;
                    }
                    break;
                }
                case 30: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(38);
                            break;
                        }
                        case 1:
                            this.sendWrite(p, (short) 49, "Mã quà tặng:");
                            break;
                        case 2: {
                            if (optionId == 0) {
                                this.server.manager.rotationluck[0].luckMessage(p);
                                break;
                            }
                            if (optionId == 2) {
                                this.server.manager.sendTB(p, "Vòng xoay vip", "bạn có thể đặt cược từ 1 triệu xu tới 50 triệu xu");
                                break;
                            }
                            break;
                        }
                        case 3: {
                            if (optionId == 0) {
                                this.server.manager.rotationluck[1].luckMessage(p);
                                break;
                            }
                            if (optionId == 2) {
                                this.server.manager.sendTB(p, "Vòng xoay lượng", "bạn có thể tham gia từ 1000 lượng tới 100.000 lượng");
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 32: {
                    switch (menuId) {
                        case 0: {
                            switch (optionId) {
                                case 0: {
                                    // Chien truong keo Tham gia
                                    Server.candyBattleManager.enter(p.nj);
                                    break;
                                }
                                case 1: {
                                    // Chien truong keo huong dan
                                    Service.sendThongBao(p.nj, "Chiến trường kẹo:\n"
                                            + "\t- 20 ninja sẽ chia làm 2 đội Kẹo Trăng và Kẹo Đen.\n"
                                            + "\t- Mỗi đội chơi sẽ có nhiệm vụ tấn công giở kẹo của đối phương, nhặt kẹo và sau đó chạy về bỏ vào giỏ kẹo của bên đội mình.\n"
                                            + "\t- Trong khoảng thời gian ninja giữ kẹo sẽ bị mất một lượng HP nhất định theo thời gian.\n"
                                            + "\t- Giữ càng nhiều thì nguy hiểm càng lớn.\n"
                                            + "\t- Còn 10 phú cuối cùng sẽ xuất hiện Phù Thuỷ");
                                    break;
                                }
                            }
                            break;
                        }
                        case 1: {
                            // Option 1
                            val clanManager = ClanManager.getClanByName(p.nj.clan.clanName);
                            if (clanManager != null) {
                                // Có gia tọc và khong battle
                                if (clanManager.getClanBattle() == null) {
                                    //  Chua duoc moi battle
                                    if (p.nj.getClanBattle() == null) {
                                        // La toc truong thach dau
                                        if (p.nj.clan.typeclan == TOC_TRUONG) {
                                            if (clanManager.getClanBattleData() == null
                                                    || (clanManager.getClanBattleData() != null && clanManager.getClanBattleData().isExpired())) {
                                                sendWrite(p, (byte) 4, "Nhập vào gia tộc muốn chiến đấu");
                                            } else {
                                                if (clanManager.restore()) {
                                                    enterClanBattle(p, clanManager);
                                                } else {
                                                    p.nj.getPlace().chatNPC(p, (short) 32, "Không hỗ trợ");
                                                }
                                            }
                                        } else {
                                            // Thử tìm battle data
                                            p.nj.getPlace().chatNPC(p, (short) 32, "Không hỗ trợ");
                                        }
                                    }
                                } else {
                                    enterClanBattle(p, clanManager);
                                }
                            }
                            break;
                        }
                        case 4: {
                            if (optionId == 0) {
                                p.openUI(43);
                            } else if (optionId == 1) {
                                p.openUI(44);
                                break;
                            } else if (optionId == 2) {
                                p.openUI(45);
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                //tiennu
                case 33: {
                    if (p.typemenu != 33) {
                        break;
                    }
                    switch (this.server.manager.EVENT) {
                        case 1: {
                            switch (menuId) {
                                case 0: {
                                    if (p.nj.quantityItemyTotal(432) < 1 || p.nj.quantityItemyTotal(428) < 3 || p.nj.quantityItemyTotal(429) < 2 || p.nj.quantityItemyTotal(430) < 3) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(434);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(432, 1);
                                    p.nj.removeItemBags(428, 3);
                                    p.nj.removeItemBags(429, 2);
                                    p.nj.removeItemBags(430, 3);
                                    break;
                                }
                                case 1: {
                                    if (p.nj.quantityItemyTotal(433) < 1 || p.nj.quantityItemyTotal(428) < 2 || p.nj.quantityItemyTotal(429) < 3 || p.nj.quantityItemyTotal(431) < 2) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(435);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(433, 1);
                                    p.nj.removeItemBags(428, 2);
                                    p.nj.removeItemBags(429, 3);
                                    p.nj.removeItemBags(431, 2);
                                    break;
                                }
                            }
                            break Label_6355;
                        }
                        case 2: {
                            switch (menuId) {
                                //hopbanhthuong
                                case 0: {
                                    if (p.nj.quantityItemyTotal(304) < 1 || p.nj.quantityItemyTotal(298) < 1 || p.nj.quantityItemyTotal(299) < 1 || p.nj.quantityItemyTotal(300) < 1 || p.nj.quantityItemyTotal(301) < 1) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(302);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(304, 1);
                                    p.nj.removeItemBags(298, 1);
                                    p.nj.removeItemBags(299, 1);
                                    p.nj.removeItemBags(300, 1);
                                    p.nj.removeItemBags(301, 1);
                                    break;
                                }
                                //hopbanhcaocap
                                case 1: {
                                    if (p.nj.quantityItemyTotal(305) < 1 || p.nj.quantityItemyTotal(298) < 1 || p.nj.quantityItemyTotal(299) < 1 || p.nj.quantityItemyTotal(300) < 1 || p.nj.quantityItemyTotal(301) < 1) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(303);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(305, 1);
                                    p.nj.removeItemBags(298, 1);
                                    p.nj.removeItemBags(299, 1);
                                    p.nj.removeItemBags(300, 1);
                                    p.nj.removeItemBags(301, 1);
                                    break;
                                }
                                //lambanh
                                case 2: {
                                    if (p.nj.yen < 10000 || p.nj.quantityItemyTotal(292) < 1 || p.nj.quantityItemyTotal(293) < 1 || p.nj.quantityItemyTotal(294) < 1 || p.nj.quantityItemyTotal(295) < 1 || p.nj.quantityItemyTotal(297) < 1) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(298);
                                    p.nj.addItemBag(true, it);
                                    p.nj.upyenMessage(-10000L);
                                    p.nj.removeItemBags(292, 1);
                                    p.nj.removeItemBags(293, 1);
                                    p.nj.removeItemBags(294, 1);
                                    p.nj.removeItemBags(295, 1);
                                    p.nj.removeItemBags(297, 1);
                                    break;
                                }
                                case 3: {
                                    if (p.nj.yen < 10000 || p.nj.quantityItemyTotal(294) < 1 || p.nj.quantityItemyTotal(295) < 1 || p.nj.quantityItemyTotal(297) < 1) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(299);
                                    p.nj.addItemBag(true, it);
                                    p.nj.upyenMessage(-10000L);
                                    p.nj.removeItemBags(294, 1);
                                    p.nj.removeItemBags(295, 1);
                                    p.nj.removeItemBags(297, 1);
                                    break;
                                }
                            }
                        }

                        // bắt đầu sự kiện
                        case 3: {
                            switch (menuId) {
                                case 0:
                                    this.sendWrite(p, (short) 66, "Bánh dâu tây");
                                    break;
                                case 1:
                                    this.sendWrite(p, (short) 67, "Bánh socola");
                                    break;
                                case 2:
                                    switch (optionId) {
                                        case 0: {
                                    p.typemenu = ((optionId == 0) ? 233 : 300);
                                    this.doMenuArray(p, new String[]{"PET ứng long", "PET phượng hoàng băng", "Điểm săn boss"});
                                    break;
                                        }
                                }
                                    break;
                                case 3:
                                    this.server.manager.sendTB(p, "Top làm bánh socola", BXHManager.getStringBXH(4));
                                    break;
                                case 4:
                                    server.manager.sendTB(p, "Hướng dẫn", "-Điểm đua top : " + p.nj.topSK + " điểm\n\n"
                                           + "Đổi bánh dâu : 1 bơ + 1 kem + 1 đường bột + 1 dâu (mua goosho) \n"
                                            + "Đổi bánh socola : 1 bơ + 1 kem + 1 đường bột + 1 chocolate (mua goosho) \n\n"
                                            + "Đổi pet vĩnh viễn : điểm boss + lượng  \n\n"
                                            + "Đua top làm bánh socola  \n"
                                            + "- Top 1 (trên 100.000đ) : \n"
                                            + "+ 4 viên ngọc 10 khác loại \n"
                                            + "+ PET hỏa kỳ lân vĩnh viễn \n"
                                            + "- Top 2 (trên 100.000đ) : \n"
                                            + "+ 4 viên ngọc 10 khác loại \n"
                                            + "- Top 3 -> 10 : \n"
                                            + "+ PET ứng long vĩnh viễn \n"
                                            + "+ PET hỏa kỳ lân 1 tháng");
                                    break;
                            }
                        }
                        break Label_6355;
                        default: {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại chưa có sự kiện diễn ra");
                            break Label_6355;
                        }
                    }
                }
            //npc đổi đồ sự kiện
                case 45:
                    switch (menuId) {
                        case 0:
                            if (p.nj.topSK2 < 5000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ 5000 điểm sự kiện");
                                break;
                            }  else if (p.nj.getAvailableBag() < 6) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ 6 chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(1026);
                                it.isExpires = true;
                                it.expires = util.TimeDay(30);
                                p.nj.topSK2 -= 5000;
                                p.nj.addItemBag(true, it);
                                break;
                            }
                        case 1: {
                            if (p.nj.topSK2 < 20000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ 20000 điểm sự kiện");
                                break;
                            } else if (p.nj.getAvailableBag() < 6) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ 6 chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(1027);
                                it.isExpires = true;
                                it.expires = util.TimeDay(30);
                                p.nj.topSK2 -= 20000;
                                p.nj.addItemBag(true, it);
                                break;
                            }
                        }
                        case 2: { 
                            this.server.manager.sendTB(p, "Hướng Dẫn", "-Điểm ăn bánh : " + p.nj.topSK2 + " điểm\n\n "
                                    + "+ Mỗi lần ăn bánh socola bạn sẽ có 1 điểm\n"
                                    + "+ Điểm này không liên quan điểm đua top kia nên ae có cứ đổi nha \n\n"
                                    + "+ 5000đ = Gậy phép sử dụng ra đồ hiếm có hạn 1 2 tháng\n"
                                    + "+ 20000đ = Chổi bay sử dụng ra đồ hiếm có tỉ lệ vĩnh viễn");
                            break;
                        }
                    }
                    break;

                // đổi pet vĩnh viễn
                case 233:
                    switch (menuId) {
                        case 0:
                            if (p.nj.topSK1 < 50) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ 50 điểm săn boss");
                                break;
                            } else  if (p.luong < 5000) {
                            p.session.sendMessageLog("Bạn không có đủ 5000 lượng");
                            break;
                        } else if (p.nj.getAvailableBag() <6) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ 6 chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(852);
                                it.isExpires = false;
                                p.upluongMessage(-5000);
                                p.nj.topSK1 -= 50;
                                p.nj.addItemBag(true, it);
                                break;
                            }
                        case 1: {
                            if (p.nj.topSK1 < 100) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ 100 điểm săn boss");
                                break;
                            } else  if (p.luong < 5000) {
                                p.session.sendMessageLog("Bạn không có đủ 5000 lượng");
                                break;
                            } else if (p.nj.getAvailableBag() <6) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ 6 chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(839);
                                it.isExpires = false;
                                p.upluongMessage(-5000);
                                p.nj.topSK1 -= 100;
                                p.nj.addItemBag(true, it);
                                break;
                            }
                        }
                        case 2: {
                            this.server.manager.sendTB(p, "Hướng Dẫn", "-Điểm săn boss :" + p.nj.topSK1 + " điểm\n\n + Mỗi lần săn boss người tuyết bạn có 10 điểm\n"
                                    + "+ 50đ + 5000 lượng = pet ứng long vĩnh viễn\n"
                                    + "+ 100đ + 5000 lượng = pet phượng hoàng băng vĩnh viễn");
                            break;
                        }
                    }
                    break;
                // đổi lồng đèn
                case 221:
                    switch (menuId) {
                        case 0: {
                            if (p.nj.ItemBody[10] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng 1 trong 4 loại lồng đèn để có thể hoán đổi.");
                                break;
                            }
                            if (p.nj.ItemBody[10].id != 568 && p.nj.ItemBody[10].id != 569 && p.nj.ItemBody[10].id != 570 && p.nj.ItemBody[10].id != 571) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng 1 trong 4 loại lồng đèn để có thể hoán đổi.");
                                break;
                            }
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 6) {
                                p.session.sendMessageLog("Hành trang không đủ 6 chỗ trống");
                                break;
                            }
                            if (p.luong < 500) {
                                p.session.sendMessageLog("Bạn không có đủ 500 lượng");
                                break;
                            }
                            final Item it = ItemData.itemDefault(util.nextInt(992, 999));
                            int a = 0;
                            for (int i = 0; i < GameScr.optionden.length; i++) {
                                if (util.nextInt(1, 10) < 3) {
                                    it.option.add(new Option(GameScr.optionden[i], util.nextInt(GameScr.paramden[i], GameScr.paramden[i] * 70 / 100)));
                                    a++;
                                }
                            }
                            it.setLock(true);
                            p.nj.addItemBag(true, it);
                            if (p.nj.ItemBody[10].id == 568) {
                                p.removeEffect(38);
                            } else if (p.nj.ItemBody[10].id == 569) {
                                p.removeEffect(36);
                            } else if (p.nj.ItemBody[10].id == 570) {
                                p.removeEffect(37);
                            } else if (p.nj.ItemBody[10].id == 571) {
                                p.removeEffect(39);
                            }
                            p.nj.removeItemBody((byte) 10);
                            p.upluongMessage(-500);
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hoán đổi lồng đèn thành công.");
                            break;
                        }
                        case 1: {
                            if (p.nj.ItemBody[10] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng 1 trong 4 loại lồng đèn để có thể hoán đổi.");
                                break;
                            }
                            if (p.nj.ItemBody[10].id != 568 && p.nj.ItemBody[10].id != 569 && p.nj.ItemBody[10].id != 570 && p.nj.ItemBody[10].id != 571) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng 1 trong 4 loại lồng đèn để có thể hoán đổi.");
                                break;
                            }
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 6 ) {
                                p.session.sendMessageLog("Hành trang không đủ 6 chỗ trống");
                                break;
                            }
                            if (p.nj.xu < 10000000) {
                                p.session.sendMessageLog("Bạn không có đủ 10tr xu");
                                break;
                            }
                            final Item it = ItemData.itemDefault(util.nextInt(992, 999));
                            int a = 0;
                            for (int i = 0; i < GameScr.optionden.length; i++) {
                                if (util.nextInt(1, 10) < 3) {
                                    it.option.add(new Option(GameScr.optionden[i], util.nextInt(GameScr.paramden[i], GameScr.paramden[i] * 70 / 100)));
                                    a++;
                                }
                            }
                            it.setLock(true);
                            p.nj.addItemBag(true, it);
                            if (p.nj.ItemBody[10].id == 568) {
                                p.removeEffect(38);
                            } else if (p.nj.ItemBody[10].id == 569) {
                                p.removeEffect(36);
                            } else if (p.nj.ItemBody[10].id == 570) {
                                p.removeEffect(37);
                            } else if (p.nj.ItemBody[10].id == 571) {
                                p.removeEffect(39);
                            }
                            p.nj.removeItemBody((byte) 10);
                            p.nj.upxuMessage(-10000000);
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hoán đổi lồng đèn thành công.");
                            break;
                        }
                    }
                    break;

//                case 223:
//                    switch (menuId) {
//                        case 0: {
//                            this.sendWrite(p, (short) 6, "Bánh thập cẩm");
//                            break;
//                        }
//                        case 1: {
//                            this.sendWrite(p, (short) 7, "Bánh dẻo");
//                            break;
//                        }
//                        case 2: {
//                            this.sendWrite(p, (short) 8, "Bánh đậu xanh");
//                            break;
//                        }
//                        case 3: {
//                            this.sendWrite(p, (short) 9, "Bánh pía");
//                            break;
//                        }
//                    }
//                    break;
//                case 225:
//                    switch (menuId) {
//                        case 0: {
//                            this.sendWrite(p, (short) 66, "Hộp bánh thường");
//                            break;
//                        }
//                        case 1: {
//                            this.sendWrite(p, (short) 67, "Hộp bánh thượng hạng");
//                            break;
//                        }
//                        case 2: {
//                            server.manager.sendTB(p, "Hướng dẫn", "hihi");
//                            break;
//                        }
//                    }
//                    break;

//                    // đổi quà menu tiên nữ

                case 227:
                    switch (menuId) {
                        case 0:
                            if (p.nj.quantityItemyTotal(611) < 200) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 200 kẹo táo");
                                break;
                            } else if (p.luong < 200) {
                                p.session.sendMessageLog("Chưa đủ 200 lượng nhé con");
                                break;
                            } else if (p.nj.getAvailableBag() == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(804 + p.nj.gender);
                                p.upluongMessage(-200L);
                                it.isExpires = true;
                                it.expires = util.TimeDay(7);
                                p.nj.addItemBag(true, it);
                                p.nj.removeItemBags(611, 200);
                                break;
                            }
//                        case 1: {
//                            if (p.nj.quantityItemyTotal(309) < 10) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 10 bánh trung thu băng hỏa");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(800 - p.nj.gender); 
//                                it.isExpires = true;
//                                it.expires = util.TimeDay(30);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(309, 10);
//                                break;
//                            }
//                        }
//                        case 2: {
//                            if (p.nj.quantityItemyTotal(308) < 10) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 10 bánh trung thu phong lôi");
//                                break;
//                            } else if (p.nj.getAvailableBag() == 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
//                            } else {
//                                Item it = ItemData.itemDefault(851); 
//                                it.isExpires = true;
//                                it.expires = util.TimeDay(30);
//                                p.nj.addItemBag(true, it);
//                                p.nj.removeItemBags(308, 10);
//                                break;
//                            }
//                        }
                    }
                    break;

                case 229:
                    switch (menuId) {
                        case 0: {
                            this.server.manager.sendTB(p, "Top Event", BXHManager.getStringBXH(5));
                            break;
                        }
                        case 1: {
                            server.manager.sendTB(p, "Quà đua top", "- Đua top từ 1/9 đến hết 22h 20/09 \n\n-Top 1 : \n + Hakairo Yoroi vĩnh viễn \n+ thời trang trung thu vĩnh viễn \n + Vũ khí thời trang vĩnh viễn \n + 2 rương huyền bí và pet bạch hổ 3 tháng \n\n-Top 2 : \n + Hakairo Yoroi vĩnh viễn \n+ thời trang trung thu vĩnh viễn \n + Vũ khí thời trang 6 tháng  \n+ 1 rương huyền bí và pet bạch hổ 2 tháng  \n\n-Top 3 đến Top 5 : \n + Hakairo Yoroi vĩnh viễn \n+ thời trang trung thu vĩnh viễn  \n + 1 rương bạch ngân và pet bạch hổ 1 tháng \n\n-Top 6 đến Top 10 : \n + Pet lân vĩnh viễn \n + Hakairo Yoroi 3 tháng \n+ thời trang trung thu 3 tháng  \n + 2 bát bảo \n\n-Yêu cầu : Top1,Top2 cần đạt ít nhất 100.000 điểm trở lên mới có thể nhận thưởng");
                            break;
                        }
                        case 2: {
                            this.server.manager.sendTB(p, "Hướng Dẫn", "- Để đua top thả đèn bạn mua tại npc Goosho giá 10 lượng \n\n- Mỗi lần thả đèn các bạn sẽ nhận được 1 điểm xếp hạng và 1 phần quà ngẫu nhiên\n\n- -Yêu cầu : Top1,Top2 cần đạt ít nhất 100.000 điểm trở lên mới có thể nhận thưởng");
                            break;
                        }
                    }
                    break;

                // sư kiện phụ

//                case 231: {
//                    switch (menuId) {
//                        case 0: {
//                            if (p.nj.getAvailableBag() == 0) {
//                                p.sendYellowMessage("Hành trang không đủ chỗ trống");
//                                return;
//                            } else if (p.nj.getLevel() < 10) {
//                                p.sendYellowMessage("Yêu cầu trình độ đạt cấp 10");
//                                return;
//                            } else if (p.nj.quantityItemyTotal(856) <= 0) {
//                                p.sendYellowMessage("Bạn không có đủ gỗ lim có may mắn để tham gia");
//                                return;
//                            } else {
//                                Item itemup;
//                                int henxui = util.nextInt(1000);
//                                if (henxui < 100) {
//                                    p.nj.topSK2 += 1;
//                                    p.updateExp(500000L, true);
//                                    short[] arId = new short[]{9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 281, 383, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453, 337, 338, 567, 477, 477, 684, 684, 775, 775, 788, 788, 789, 789, 778, 778, 778, 778, 778, 778, 778, 541, 542, 852, 852, 795, 796, 798, 804, 805};
//                                    short idI = arId[util.nextInt(arId.length)];
//                                    itemup = ItemData.itemDefault(idI);
//                                    itemup.isExpires = true;
//                                    itemup.expires = util.TimeDay(7);
//                                    p.nj.addItemBag(true, itemup);
//                                    if (idI == 383) {
//                                        server.manager.chatKTG(p.nj.name + " tham gia xây dựng nhận được Bát bảo");
//                                    }
//                                    if (idI == 852) {
//                                        server.manager.chatKTG(p.nj.name + " tham gia xây dựng nhận được Pet ứng long");
//                                    }
//                                    p.nj.removeItemBags(856, 1);
//                                    break;
//                                } else {
//                                    p.nj.topSK2 += 1;
//                                    p.updateExp(500000L, true);
//                                    short[] arId = new short[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453, 337, 338, 567, 477, 684, 684, 775, 775, 788, 788, 789, 789, 778, 778, 778, 778, 778, 778, 778, 541, 542};
//                                    short idI = arId[util.nextInt(arId.length)];
//                                    itemup = ItemData.itemDefault(idI);
//                                    itemup.isLock = true;
//                                    itemup.expires = util.TimeDay(7);
//                                    itemup.isExpires = true;
//                                    p.nj.addItemBag(true, itemup);
//                                    if (idI == 383) {
//                                        server.manager.chatKTG(p.nj.name + " tham gia xây dựng nhận được Bát bảo");
//                                    }
//                                    if (idI == 852) {
//                                        server.manager.chatKTG(p.nj.name + " tham gia xây dựng nhận được Pet ứng long");
//                                    }
//                                    p.nj.removeItemBags(856, 1);
//                                    break;
//                                }
//                            }
//                        }
//                        case 1: {
//                            server.manager.sendTB(p, "Hướng dẫn", "- Trong thời gian diễn ra sự kiện hè sẽ diễn ra sự kiện chung sức xây dựng\n\n- Mỗi lần tham gia các bạn sẽ nhận được 1 điểm đua top xây dựng\n\n"
//                                    + "- Các bạn còn có thể sử dụng Gỗ lim (NPC Goosho) để tham gia hoạt động chung sức xây dựng.");
//                            break;
//                        }
//                    }
//                }
//                break;

                case 46: {
                            switch (menuId) {
                                case 0: {
                                    if (p.nj.isNhanban) {
                                        p.session.sendMessageLog("Phân thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    if (p.nj.ItemBody[10] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet ứng long.");
                                        break;
                                    }
                                    if (p.nj.ItemBody[10].id != 852) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet ứng long.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() < 6) {
                                        p.session.sendMessageLog("Hành trang không đủ 6 chỗ trống");
                                        break;
                                    }
                                    if (p.luong < 300) {
                                        p.session.sendMessageLog("Bạn không có đủ 300 lượng");
                                        break;
                                    }
                                    final Item item = p.nj.ItemBody[10];
                                    final Item it = ItemData.itemDefault(852);
                                    int a = 0;
                                    for (int i = 0; i < GameScr.optionPet.length; i++) {
                                        if (util.nextInt(1, 10) < 3) {
                                            it.option.add(new Option(GameScr.optionPet[i], util.nextInt(GameScr.paramPet[i], GameScr.paramPet[i] * 70 / 100)));
                                            a++;
                                        }
                                    }
                                    if (item.expires > 0) {
                                        it.isExpires = true;
                                        it.expires = item.expires;
                                    }
                                    it.setLock(true);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBody((byte) 10);
                                    p.upluongMessage(-300);
                                    String b = "";
                                    if (a > 5) {
                                        b = "Ổn chứ bro";
                                    } else if (a > 2) {
                                        b = "Tạm thế nhá";
                                    } else {
                                        b = "Hết mức rồi";
                                    }
                                    p.nj.getPlace().chatNPC(p, (short) 40, b);
                                    break;
                                }
                                case 1: {
                                    if (p.nj.ItemBody[10] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet ứng long.");
                                        break;
                                    }
                                    if (p.nj.ItemBody[10].id != 852) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet ứng long.");
                                        break;
                                    }
                                    if (p.nj.isNhanban) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() <6) {
                                        p.session.sendMessageLog("Hành trang không đủ 6 chỗ trống");
                                        break;
                                    }
                                    if (p.luong < 1000) {
                                        p.session.sendMessageLog("Bạn không có đủ 1000 lượng");
                                        break;
                                    }
                                    Item it = p.nj.ItemBody[10];
                                    if (it.getUpgrade() >= 16) {
                                        p.session.sendMessageLog("Pet đã đạt cấp tối đa");
                                        break;
                                    }
                                    if (GameScr.percentPet[it.getUpgrade()] >= util.nextInt(100)) {
                                        for (byte k = 0; k < it.option.size(); ++k) {
                                            final Option option = it.option.get(k);
                                            option.param += option.param * 10 / 100;
                                        }
                                        it.setUpgrade(it.getUpgrade() + 1);
                                        it.setLock(true);
                                        p.nj.addItemBag(true, it);
                                        p.sendYellowMessage("Nâng cấp thành công!");
                                        p.nj.removeItemBody((byte) 10);
                                    } else {
                                        p.sendYellowMessage("Nâng cấp thất bại!");
                                    }
                                    p.upluongMessage(-1000);
                                    break;
                                }
                                case 2: {
                                    if (p.nj.ItemBody[10] == null) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet ứng long.");
                                        break;
                                    }
                                    if (p.nj.ItemBody[10].id != 852) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi hãy sử dụng pet ứng long.");
                                        break;
                                    }
                                    if (p.nj.isNhanban) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này.");
                                        break;
                                    }
                                    p.nj.removeItemBody((byte) 10);
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn đã hủy pet thành công");
                                    break;
                                }
                                case 3: {
                                    this.server.manager.sendTB(p, "Hướng Dẫn Nâng Pet", "1. Đây là npc pet trang bị 1 \n\n 2. Luyện pet để săn chỉ số mất 300 lượng  \n\n 3. Nâng pet mất 1000 lượng và tỉ lệ nâng từ 1 tới 16 như nhau đều 90%");
                                    break;
                                }
                            }
                            break;
                        }




                //cpanel
                case -125:
                    if (menuId == 0) { //Item
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 55, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 1) { //Xu
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 60, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 2) { //Lượng
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 58, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 3) { //yên
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 62, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 4) { //Mess
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 64, "Nhập tên tài khoản:");
                            break;
                        }
                    }
                    break;
                case 92: {
                    p.typemenu = ((menuId == 0) ? 93 : 94);
                    this.doMenuArray(p, new String[]{"Thông tin", "Luật chơi"});
                    break;
                }
                case 93: {
                    if (menuId == 0) {
                        this.server.manager.rotationluck[0].luckMessage(p);
                        break;
                    }
                    if (menuId == 1) {
                        this.server.manager.sendTB(p, "Vòng xoay vip", "vào đây làm giàu dễ lém");
                        break;
                    }
                    break;
                }
                case 94: {
                    if (menuId == 0) {
                        this.server.manager.rotationluck[1].luckMessage(p);
                        break;
                    }
                    if (menuId == 1) {
                        this.server.manager.sendTB(p, "Vòng xoay lượng", "hảo hán mới dám chơi");
                        break;
                    }
                    break;
                }
                case 95: {
                    break;
                }
                case 120: {
                    if (menuId > 0 && menuId < 7) {
                        p.Admission(menuId);
                        break;
                    }
                    break;
                }
                case 23: {
                    // Matsurugi
                    if (ninja.getTaskId() == 23 && ninja.getTaskIndex() == 1 && menuId == 0) {
                        boolean hasItem = false;
                        for (Item item : p.nj.ItemBag) {
                            if (item != null && item.id == 230) {
                                hasItem = true;
                                break;
                            }
                        }

                        if (!hasItem) {
                            val i = ItemData.itemDefault(230);
                            i.setLock(true);
                            p.nj.addItemBag(false, i);
                            p.nj.getPlace().chatNPC(p, 23, "Ta hi vọng đây là lần cuối ta giao chìa khoá cho con ta nghĩ lần này con sẽ làm được. ");
                        } else {
                            p.nj.getPlace().chatNPC(p, 23, "Con đã có chìa khoá rồi không thể nhận thêm được");
                        }
                    } else {
                        p.nj.getPlace().chatNPC(p, 23, "Ta không quen biết con con đi ra đi");
                    }
                    break;
                }
                case 20: {
                    // Soba
                    if (menuId == 0) {
                        if (!ninja.hasItemInBag(266)) {
                            if (ninja.getTaskId() == 32 && ninja.getTaskIndex() == 1) {
                                val item = ItemData.itemDefault(266);
                                item.setLock(true);
                                ninja.addItemBag(false, item);
                            }
                        } else {
                            ninja.p.sendYellowMessage("Con đã có cần câu không thể nhận thêm");
                        }
                    } else {
                        ninja.getPlace().chatNPC(ninja.p, 20, "Làng ta rất thanh bình con có muốn sống ở đây không");
                    }
                    break;
                }
                case 28: {
                    // Shinwa
                    switch (menuId) {
                        case 0: {
                            final List<ItemShinwa> itemShinwas = items.get((int) optionId);
                            Message mess = new Message(103);
                            mess.writer().writeByte(optionId);
                            if (itemShinwas != null) {
                                mess.writer().writeInt(itemShinwas.size());
                                for (ItemShinwa item : itemShinwas) {
                                    val itemStands = item.getItemStand();
                                    mess.writer().writeInt(itemStands.getItemId());
                                    mess.writer().writeInt(itemStands.getTimeEnd());
                                    mess.writer().writeShort(itemStands.getQuantity());
                                    mess.writer().writeUTF(itemStands.getSeller());
                                    mess.writer().writeInt(itemStands.getPrice());
                                    mess.writer().writeShort(itemStands.getItemTemplate());
                                }
                            } else {
                                mess.writer().writeInt(0);
                            }
                            mess.writer().flush();
                            p.sendMessage(mess);
                            mess.cleanup();
//                            p.session.sendMessageLog("Tạm bảo trì");
                            break;
                        }
                        case 1: {
                            // Sell item
                            p.openUI(36);
//                            p.session.sendMessageLog("Tạm bảo trì");
                            break;
                        }
                        case 2: {
                            // Get item back

                            for (ItemShinwa itemShinwa : items.get(-2)) {
                                if (p.nj.getAvailableBag() == 0) {
                                    p.sendYellowMessage("Hành trang không đủ ô trống để nhận thêm");
                                    break;
                                }
                                if (itemShinwa != null) {
                                    if (p.nj.name.equals(itemShinwa.getSeller())) {
                                        itemShinwa.item.quantity = itemShinwa.getQuantity();
                                        p.nj.addItemBag(true, itemShinwa.item);
                                        items.get(-2).remove(itemShinwa);
                                        deleteItem(itemShinwa);
                                    }
                                }
                            }

                            break;
                        }
                    }
                    break;
                }
                case 27: {
                    // Cam chia khoa co quan
                    if (Arrays.stream(p.nj.ItemBag).anyMatch(item -> item != null && (item.id == 231 || item.id == 260))) {
                        p.nj.removeItemBags(231, 1);
                        p.nj.removeItemBags(260, 1);
                        p.getClanTerritoryData().getClanTerritory().plugKey(p.nj.getMapid(), p.nj);

                    } else {
                        p.sendYellowMessage("Không có chìa khoá để cắm");
                    }
                    break;
                }

//                case 24: {
//                    switch (menuId) {
//                        case 0: {
//                            if (p.luong <= 500) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 500 lượng để đổi 5tr xu");
//
//                                return;
//                            } else {
//                                p.nj.upxuMessage(5000000);
//                                p.upluongMessage(-500L);
//                                return;
//                             }                            
//                        }
//                        case 1 : {
//                            if (p.luong <= 500) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 500 lượng để đổi 5tr5 yên");
//
//                                    return;
//                                } else {
//                                    p.nj.upyenMessage(5500000);
//                                    p.upluongMessage(-500L);
//                                    return;
//                                }
//                        }
//                    }
//                    break;
//                }
//                Menu npc Okenachan
                case 24:
                    switch (menuId) {
                        case 0:
                            if (optionId == 0) {
                                this.sendWrite(p, (short) 10, "Nhập số lần đổi xu");
                                break;
                            }
                            if (optionId == 1) {
                                this.sendWrite(p, (short) 11, "Nhập số lần đổi yên");
                                break;
                            }

                        case 1:
                            if (optionId == 0) {
                                if (p.nj.getLevel() < 10) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 10 đi đã bạn");
                                } else if (p.nj.quacap10 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap10 = 2;
                                    p.nj.upyenMessage(10000000);
                                    p.nj.upxuMessage(100000);
                                    p.upluongMessage(100);
                                }

                            }
                            //level 20
                            if (optionId == 1) {
                                if (p.nj.getLevel() < 20) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 20 đi đã bạn");
                                } else if (p.nj.quacap20 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap20 = 2;
                                    p.nj.upyenMessage(20000000);
                                    p.nj.upxuMessage(200000);
                                    p.upluongMessage(200);
                                }

                            }
                            //level 30
                            if (optionId == 2) {
                                if (p.nj.getLevel() < 30) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 30 đi đã bạn");
                                } else if (p.nj.quacap30 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap30 = 2;
                                    p.nj.upyenMessage(30000000);
                                    p.nj.upxuMessage(300000);
                                    p.upluongMessage(300);
                                }

                            }
                            //level 40
                            if (optionId == 3) {
                                if (p.nj.getLevel() < 40) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 40 đi đã bạn");
                                } else if (p.nj.quacap40 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap40 = 2;
                                    p.nj.upyenMessage(40000000);
                                    p.nj.upxuMessage(400000);
                                    p.upluongMessage(400);
                                }

                            }
                            //level 50
                            if (optionId == 4) {
                                if (p.nj.getLevel() < 50) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 50 đi đã bạn");
                                } else if (p.nj.quacap50 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap50 = 2;
                                    p.nj.upyenMessage(50000000);
                                    p.nj.upxuMessage(500000);
                                    p.upluongMessage(500);

//                                    Item it = new Item();
//                                    it.id = 383;
//                                    it.quantity = 1;
//                                    it.isLock = true;
//                                    p.nj.addItemBag(true, it);
                                }

                            }
                            //level 60
                            if (optionId == 5) {
                                if (p.nj.getLevel() < 60) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 60 đi đã bạn");
                                } else if (p.nj.quacap60 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap60 = 2;
                                    p.nj.upyenMessage(60000000);
                                    p.nj.upxuMessage(600000);
                                    p.upluongMessage(600);

                                }

                            }

                            //level 70
                            if (optionId == 6) {
                                if (p.nj.getLevel() < 70) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 70 đi đã bạn");
                                } else if (p.nj.quacap70 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap70 = 2;
                                    p.nj.upyenMessage(70000000);
                                    p.nj.upxuMessage(700000);
                                    p.upluongMessage(700);

                                }

                            }
                            //level 80
                            if (optionId == 7) {
                                if (p.nj.getLevel() < 80) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 80 đi đã bạn");
                                } else if (p.nj.quacap80 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap80 = 2;
                                    p.nj.upyenMessage(80000000);
                                    p.nj.upxuMessage(800000);
                                    p.upluongMessage(800);

                                }

                            }
                            //level 90
                            if (optionId == 8) {
                                if (p.nj.getLevel() < 90) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 90 đi đã bạn");
                                } else if (p.nj.quacap90 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap90 = 2;
                                    p.nj.upyenMessage(90000000);
                                    p.nj.upxuMessage(900000);
                                    p.upluongMessage(900);

                                }

                            }
                            //level 100
                            if (optionId == 9) {
                                if (p.nj.getLevel() < 100) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đạt lever 100 đi đã bạn");
                                } else if (p.nj.quacap100 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap100 = 2;
                                    p.nj.upyenMessage(100000000);
                                    p.nj.upxuMessage(1000000);
                                    p.upluongMessage(1000);

                                }

                            }
                            return;

                    }
                    break;
                case 572: {
                    switch (menuId) {
                        case 0: {
                            p.typeTBLOption = $240;
                            break;
                        }
                        case 1: {
                            p.typeTBLOption = $480;
                            break;
                        }
                        case 2: {
                            p.typeTBLOption = ALL_MAP;
                            break;
                        }
                        case 3: {
                            p.typeTBLOption = PICK_ALL;
                            break;
                        }
                        case 4: {
                            p.typeTBLOption = USEFUL;
                            break;
                        }
                        case 5: {
                            p.activeTBL = !p.activeTBL;
                        }
                    }
                    break;
                }
                case 4444: {
                    if (menuId == 0) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 0) {
                            p.session.sendMessageLog("con đã học chiêu này rồi");
                            return;
                        }
                        if (p.nj.expkm < 5000000) {
                            p.session.sendMessageLog("Không đủ 5 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 1000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-1000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-1000);
                                p.nj.expkm -= 5000000;
                                p.nj.lvkm = 1;
                                p.session.sendMessageLog("con đã học thành công kinh mạch hiện tại đang là lv1");
                            }
                        }
                        break;
                    }
                    if (menuId == 1) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 1) {
                            p.session.sendMessageLog("Mở kinh mạch đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 10000000) {
                            p.session.sendMessageLog("Không đủ 10 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 2000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-2000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-2000);
                                p.nj.expkm -= 10000000;
                                p.nj.lvkm = 2;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv2");
                            }
                        }
                        break;
                    }
                    if (menuId == 2) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 2) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 2 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 15000000) {
                            p.session.sendMessageLog("Không đủ 15 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 3000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-3000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-3000);
                                p.nj.expkm -= 15000000;
                                p.nj.lvkm = 3;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv3");
                            }
                        }
                        break;
                    }
                    if (menuId == 3) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 3) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 3 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 20000000) {
                            p.session.sendMessageLog("Không đủ 20 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 4000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-4000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-4000);
                                p.nj.expkm -= 20000000;
                                p.nj.lvkm = 4;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv4");
                            }
                        }
                        break;
                    }
                    if (menuId == 4) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 4) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 4 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 25000000) {
                            p.session.sendMessageLog("Không đủ 25 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 5000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-5000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-5000);
                                p.nj.expkm -= 25000000;
                                p.nj.lvkm = 5;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv5");
                            }
                        }
                        break;
                    }
                    if (menuId == 5) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 5) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 5 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 30000000) {
                            p.session.sendMessageLog("Không đủ 30 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 6000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-6000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-6000);
                                p.nj.expkm -= 30000000;
                                p.nj.lvkm = 6;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv6");
                            }
                        }
                        break;
                    }
                    if (menuId == 6) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 6) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 6 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 35000000) {
                            p.session.sendMessageLog("Không đủ 35 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 7000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-7000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-7000);
                                p.nj.expkm -= 35000000;
                                p.nj.lvkm = 7;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv7");
                            }
                        }
                        break;
                    }
                    if (menuId == 7) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 7) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 7 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 40000000) {
                            p.session.sendMessageLog("Không đủ 40 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 8000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-8000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-8000);
                                p.nj.expkm -= 40000000;
                                p.nj.lvkm = 8;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv8");
                            }
                        }
                        break;
                    }
                    if (menuId == 8) { //luyện chiêu hiền nhân
                        if (p.nj.lvkm != 8) {
                            p.session.sendMessageLog("Nâng kinh mạch lên cấp 8 đi rồi đến gặp tao để nâng");
                            return;
                        }
                        if (p.nj.expkm < 50000000) {
                            p.session.sendMessageLog("Không đủ 50 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                            break;
                        } else if (p.luong < 10000) {
                            p.session.sendMessageLog("Chưa đủ lượng nhé con");
                            return;
                        } else {
                            byte pkoolvn = (byte) util.nextInt(1, 100);
                            if (pkoolvn <= 70) {
                                p.upluongMessage(-10000);
                                p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                                return;
                            } else {
                                p.upluongMessage(-10000);
                                p.nj.expkm -= 50000000;
                                p.nj.lvkm = 9;
                                p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv9");
                            }
                        }
                        break;
                    }

                    if (menuId == 9) {
                        server.manager.sendTB(p, "Điều Kiện học kinh mạch", "Exp kinh mạch nhận được thông qua việc đánh tinh anh, thủ lĩnh"
                                + "\n>Kinh mạch<"
                                + "\n-Con cần  5 triệu exp Kinh mạch và 1k lượng để có thể học"
                                + "\n-lv2 cần 10 triệu exp Kinh mạch và 2k lượng"
                                + "\n-lv3 cần 15 triệu exp Kinh mạch và 3k lượng"
                                + "\n-lv4 cần 20 triệu exp Kinh mạch và 4k lượng"
                                + "\n-lv5 cần 25 triệu exp Kinh mạch và 5k lượng"
                                + "\n-lv6 cần 30 triệu exp Kinh mạch và 6k lượng"
                                + "\n-lv7 cần 35 triệu exp Kinh mạch và 7k lượng"
                                + "\n-lv8 cần 40 triệu exp Kinh mạch và 8k lượng"
                                + "\n-lv9 cần 50 triệu exp Kinh mạch và 10k lượng"
                                + "\n-thành công Kinh mạch sẽ lên lv và nhận đc hiệu ứng tương ứng"
                                + "\n-thất bại sẽ mất lượng exp giữ nguyên"
                        );
                        return;
                    }
                }
                break;
                case 4445: {// 
                    if (menuId == 0) {
                        p.Kinhmach();
                    }
                    if (menuId == 1) {
                        p.session.sendMessageLog("Số exp kinh mạch đang có là: " + p.nj.expkm);
                        return;
                    }
                }
                break;
                case 999:
                    switch (menuId) {
                        case 0: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            sendWrite(p, (short) 41_0, "Nhập tên nhân vật:");
                            break;
                        }
                        case 1: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            sendWrite(p, (short) 41_1, "Nhập tên nhân vật:");
                            break;
                        }
                    }
                    break;
                case 850: // Mảnh giấy vụn
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(251) >= 300) {
                                p.nj.removeItemBags(251, 300);
                                p.sendYellowMessage("Bạn nhận được 1 sách tiềm năng");

                                Item it = ItemData.itemDefault(253);
                                p.nj.addItemBag(true, it);
                                return;
                            } else if (p.nj.quantityItemyTotal(251) < 300) {
                                p.session.sendMessageLog("Bạn chưa đủ 300 giấy vụn để đổi sách");
                                return;
                            }
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(251) >= 250) {
                                p.nj.removeItemBags(251, 250);
                                p.sendYellowMessage("Bạn nhận được 1 sách kỹ năng");

                                Item it = ItemData.itemDefault(252);
                                p.nj.addItemBag(true, it);
                                return;
                            } else if (p.nj.quantityItemyTotal(251) < 250) {
                                p.session.sendMessageLog("Bạn chưa đủ 250 giấy vụn để đổi sách");
                                return;
                            }
                        }
                    }
                    break;
                case 1021: //item 1021
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(1001) >= 10) {
                                if (p.nj.quantityItemyTotal(1021) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh hồn đỏ");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1001, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1021, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1002);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1021, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 1");
                                return;
                            }
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(1002) >= 10) {
                                if (p.nj.quantityItemyTotal(1021) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh hồn đỏ");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1002, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1021, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1003);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1021, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 2");
                                return;
                            }
                        }
                        case 2: {
                            if (p.nj.quantityItemyTotal(1003) >= 10) {
                                if (p.nj.quantityItemyTotal(1021) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh hồn đỏ");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1003, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1021, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1004);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1021, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 3");
                                return;
                            }
                        }
                    }
                    break;
                case 1022: //item 1022
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(1004) >= 10) {
                                if (p.nj.quantityItemyTotal(1022) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh xanh lá");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1004, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1022, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1005);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1022, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 4");
                                return;
                            }
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(1005) >= 10) {
                                if (p.nj.quantityItemyTotal(1022) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh xanh lá");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1005, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1022, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1006);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1022, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 5");
                                return;
                            }
                        }
                        case 2: {
                            if (p.nj.quantityItemyTotal(1006) >= 10) {
                                if (p.nj.quantityItemyTotal(1022) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh xanh lá");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1006, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1022, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1007);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1022, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 6");
                                return;
                            }
                        }
                    }
                    break;
                case 1023: //item 1023
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(1007) >= 10) {
                                if (p.nj.quantityItemyTotal(1023) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh xanh lam");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1007, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1023, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1008);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1023, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 7");
                                return;
                            }
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(1008) >= 10) {
                                if (p.nj.quantityItemyTotal(1023) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh xanh lam");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1008, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1023, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1009);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1023, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 8");
                                return;
                            }
                        }
                        case 2: {
                            if (p.nj.quantityItemyTotal(1009) >= 10) {
                                if (p.nj.quantityItemyTotal(1023) < 1) {
                                    p.session.sendMessageLog("Bạn chưa đủ Trượng linh xanh lam");
                                    return;
                                }
                                if (p.nj.quantityItemyTotal(1024) < 10) {
                                    p.session.sendMessageLog("Bạn chưa đủ bụi linh hồn");
                                    return;
                                }
                                if (util.nextInt(100) < 50) {
                                    p.nj.removeItemBags(1009, 10);
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1023, 1);
                                    p.sendYellowMessage("Nâng cấp thành công");
                                    Item it = ItemData.itemDefault(1010);
                                    it.quantity = 1;
                                    p.nj.addItemBag(true, it);
                                } else {
                                    p.nj.removeItemBags(1024, 10);
                                    p.nj.removeItemBags(1023, 1);
                                    p.sendYellowMessage("Nâng cấp thất bại");
                                }
                                return;
                            } else {
                                p.session.sendMessageLog("Bạn chưa đủ 10 viên linh hồn cấp 9");
                                return;
                            }
                        }
                    }
                    break;
                case 9999: {
                    switch (menuId) {
                        case 0: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9998, "Nhập số phút muốn bảo trì 0->10 (0: ngay lập tức)");
                            break;
                        }
                        case 1: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9997, "Nhập số xu");
                            break;
                        }
                        case 2: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9996, "Nhập số lượng");
                            break;
                        }
                        case 3: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9995, "Nhập số yên");
                            break;
                        }
                        case 4: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9994, "Nhập số level");
                            break;
                        }
                        case 5: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9993, "Nhập số tiềm năng");
                            break;
                        }
                        case 6: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9992, "Nhập số kĩ năng");
                            break;
                        }
                        case 7: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            for (int i = 0; i < PlayerManager.getInstance().conns.size(); i++) {
                                if (PlayerManager.getInstance().conns.get(i) != null && PlayerManager.getInstance().conns.get(i).user != null) {
                                    User user = PlayerManager.getInstance().conns.get(i).user;
                                    user.flush();
                                    if (user.nj != null) {
                                        user.nj.flush();
                                        if (user.nj.clone != null) {
                                            user.nj.clone.flush();
                                        }
                                    }
                                }
                            }
                            Manager.chatKTG("Server đang lưu dữ liệu. CÓ thể gây ra hiện tượng giật lag!");
                            break;
                        }
                        case 8: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.sendWrite(p, (short) 9991, "Nhập thông báo");
                            break;
                        }
                        case 9: {
                            if (p.id != 1) {
                                p.session.sendMessageLog("Bạn không có quyền admin");
                                return;
                            }
                            this.server.manager.sendTB(p, "Thông báo", "Số người đang online: " + PlayerManager.getInstance().conns.size());
                            break;
                        }
                    }
                    break;
                }
                default: {
                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng này đang cập nhật nhé");
                    break;
                }
            }
        }
        util.Debug("byte1 " + npcId + " byte2 " + menuId + " byte3 " + optionId);
    }

    private void sendThongBaoTDB(User p, Tournament tournaments, String type) {
        val stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        for (TournamentData tournament : tournaments.getTopTen()) {
            stringBuilder.append(tournament.getRanked())
                    .append(".")
                    .append(tournament.getName())
                    .append("\n");
        }
        Service.sendThongBao(p, stringBuilder.toString());
    }

    public static java.util.Map<Byte, int[]> nangCapMat = new TreeMap<>();

    static {
        nangCapMat.put((byte) 1, new int[]{50, 2_000_000, 80, 200, 100});
        nangCapMat.put((byte) 2, new int[]{45, 3_000_000, 75, 300, 85});
        nangCapMat.put((byte) 3, new int[]{40, 5_000_000, 65, 500, 75});
        nangCapMat.put((byte) 4, new int[]{35, 7_500_000, 55, 700, 65});
        nangCapMat.put((byte) 5, new int[]{30, 8_500_000, 45, 900, 55});
        nangCapMat.put((byte) 6, new int[]{25, 10_000_000, 30, 1000, 45});
        nangCapMat.put((byte) 7, new int[]{20, 12_000_000, 25, 1200, 30});
        nangCapMat.put((byte) 8, new int[]{15, 15_000_000, 20, 1200, 25});
        nangCapMat.put((byte) 9, new int[]{10, 20_000_000, 15, 1500, 20});
    }

    private void nangMat(User p, Item item, boolean vip) throws IOException {

        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        if (item.id < 694) {
            int toneCount = (int) Arrays.stream(p.nj.ItemBag).filter(i -> i != null && i.id == item.id + 11).map(i -> i.quantity).reduce(0, Integer::sum);
            if (toneCount >= nangCapMat.get(item.getUpgrade())[0]) {

                if (vip && nangCapMat.get(item.getUpgrade())[3] > p.luong) {
                    p.sendYellowMessage("Không đủ lượng nâng cấp vật phẩm");
                    return;
                }
                if (p.nj.xu < nangCapMat.get(item.getUpgrade())[1]) {
                    p.sendYellowMessage("Không đủ xu để nâng cấp");
                    return;
                }
                val succ = util.percent(100, nangCapMat.get(item.getUpgrade())[vip ? 2 : 4]);
                if (succ) {
                    p.nj.get().ItemBody[14] = ItemData.itemDefault(item.id + 1);

                    p.nj.removeItemBags(item.id + 11, nangCapMat.get(item.getUpgrade())[0]);
                    //p.sendInfo(false);
                    p.sendYellowMessage("Nâng cấp mắt thành công bạn nhận được mắt " + p.nj.get().ItemBody[14].getData().name + p.nj.get().ItemBody[14].getUpgrade() + " đã mặc trên người");
                } else {
                    p.sendYellowMessage("Nâng cấp mắt thất bại");
                }

                if (vip) {
                    p.removeLuong(nangCapMat.get(item.getUpgrade())[3]);
                }

                p.nj.upxuMessage(-nangCapMat.get(item.getUpgrade())[1]);

            } else {
                p.sendYellowMessage("Không đủ " + nangCapMat.get(item.getUpgrade())[0] + " đá danh vọng cấp " + (item.getUpgrade() + 1) + " để nâng cấp");
            }
        } else {
            p.sendYellowMessage("Mắt được nâng cấp tối đa");
        }
    }

    private void enterClanBattle(User p, ClanManager clanManager) {
        val battle = p.nj.getClanBattle();
        if (battle == null) {
            p.sendYellowMessage("Bạn chưa được mời vào gia tộc chiến");
            return;
        }
        p.nj.setClanBattle(battle);
        if (!clanManager.getClanBattle().enter(p.nj, ClanManager.getClanByName(p.nj.clan.clanName) == ClanManager.getClanByName(battle.getClanNamePhe1()) ? IBattle.BAO_DANH_GT_BACH
                : IBattle.BAO_DANH_GT_HAC)) {
            p.nj.changeTypePk(Constants.PK_NORMAL);
        }
    }

    public void openUINpc(final User p, Message m) throws IOException {
        final short idnpc = m.reader().readShort();
        m.cleanup();
        p.nj.menuType = 0;
        p.typemenu = idnpc;



//        if (idnpc == 33 && server.manager.EVENT != 0) {
//
//            val itemNames = new String[EventItem.entrys.length + 1];
//
//            for (int i = 0; i < itemNames.length - 1; i++) {
//                itemNames[i] = EventItem.entrys[i].getOutput().getItemData().name;
//            }
//
//            itemNames[EventItem.entrys.length] = "Hướng dẫn";
//            createMenu(33, itemNames, "", p);
//        }
//tenmenu
//        if (idnpc == 24) {
//            doMenuArray(p, new String[]{"500 lượng lấy 5tr xu", "500 lượng lấy 5tr5 yên"});
//            return;
//        }
//        if (idnpc == 33) {
//            doMenuArray(p, new String[]{"Làm bánh", "Làm hộp bánh", "Đổi VKTT", "Đổi lồng đèn", "Hướng dẫn", "Đua Top"});
//            return;
//        }
        // menu npc
        if (idnpc == 33) {
            doMenuArray(p, new String[]{"Bánh dâu", "Bánh socola", "Đổi PET", "Đua top sever", "Hướng dẫn"});
            return;
        }

        if (idnpc == 45) {
            doMenuArray(p, new String[]{"Đổi gậy phép", "Đổi chổi bay", "Hướng dẫn"});
            return;
        }

        if (idnpc == 46) {
            doMenuArray(p, new String[]{"Luyện Pet", "Nâng Pet", "Hủy Pet", "Hướng dẫn"});
            return;
        }

        if (idnpc == 35) {
            doMenuArray(p, new String[]{"Hoán Pet Tbi2", "Hủy Pet", "Hướng dẫn"});
            return;
        }

        if (idnpc == 41) {
            doMenuArray(p, new String[]{"Điểm danh", "Quà tân thủ", "Đổi mật khẩu", " [Bật/Tắt] PK Âm", " Code SV"});
            return;
        }
        if (idnpc == 36) {
            doMenuArray(p, new String[]{""});
            return;
        }
        if (idnpc == 38) {
            doMenuArray(p, new String[]{"May mắn đầu xuân", "Hái Lộc", "Hướng Dẫn"});
            return;
        }

        if (idnpc == 0 && (p.nj.getPlace().map.isGtcMap() || p.nj.getPlace().map.loiDaiMap())) {
            if (p.nj.hasBattle() || p.nj.getClanBattle() != null) {
                createMenu(idnpc, new String[]{"Đặt cược", "Rời khỏi đây"}, "Con có 5 phút để xem thông tin đối phương", p);
            }

        } else if (idnpc == Manager.ID_EVENT_NPC) {
            createMenu(Manager.ID_EVENT_NPC, Manager.MENU_EVENT_NPC, Manager.EVENT_NPC_CHAT[util.nextInt(0, Manager.EVENT_NPC_CHAT.length - 1)], p);
        } else if ("baotrinpcshinwa".equals(p.nj.name) && idnpc == 28) {
            createMenu(28, new String[]{"Bảo trì", "Lưu dữ liệu"}, "Oke", p);
        } else if (idnpc == 32 && p.nj.getPlace().map.id == IBattle.BAO_DANH_GT_BACH || p.nj.getPlace().map.id == IBattle.BAO_DANH_GT_HAC) {
            createMenu(idnpc, new String[]{"Tổng kết", "Rời khỏi đây"}, "", p);
        } else {
            val ninja = p.nj;
            val npcTemplateId = idnpc;
            p.nj.menuType = 0;

            String[] captions = null;
            if (TaskHandle.isTaskNPC(ninja, npcTemplateId)) {
                captions = new String[1];
                p.nj.menuType = 1;
                if (ninja.getTaskIndex() == -1) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (TaskHandle.isFinishTask(ninja)) {
                    captions[0] = Text.get(0, 12);
                } else if (ninja.getTaskIndex() >= 0 && ninja.getTaskIndex() <= 4 && ninja.getTaskId() == 1) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskIndex() >= 1 && ninja.getTaskIndex() <= 15 && ninja.getTaskId() == 7) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskIndex() >= 1 && ninja.getTaskIndex() <= 3 && ninja.getTaskId() == 13) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskId() >= 11) {
                    captions[0] = TaskList.taskTemplates[ninja.getTaskId()].getMenuByIndex(ninja.getTaskIndex());
                }
            }
            if (ninja.getTaskId() == 23 && idnpc == 23 && ninja.getTaskIndex() == 1) {
                captions = new String[1];
                captions[0] = "Nhận chìa khoá";
            } else if (ninja.getTaskId() == 32 && idnpc == 20 && ninja.getTaskIndex() == 1) {
                captions = new String[1];
                captions[0] = "Nhận cần câu";
            }
            Service.openUIMenu(ninja, captions);
        }
    }

    @SneakyThrows
    public void selectMenuNpc(final User p, final Message m) throws IOException {

        val idNpc = (short) m.reader().readByte();
        val index = m.reader().readByte();
        if (idNpc == 0 && p.nj.getTaskId() != 13) {
            if (index == 0) {
                server.menu.sendWrite(p, (short) 3, "Nhập số tiền cược");
            } else if (index == 1) {
                if (p.nj.getBattle() != null) {
                    p.nj.getBattle().setState(Battle.BATTLE_END_STATE);
                }
            }
            //} else if (idNpc == Manager.ID_EVENT_NPC) {
            //  0: nhận lượng, 1: tắt exp, 2: bật up exp, 3: nhận thưởng level 70, 4: nhận thưởng level 90, 5: nhận thưởng lv 130
            //short featureCode = Manager.ID_FEATURES[index];
            //switch (featureCode) {
            //case 1: {
            //p.nj.get().exptype = 0;
            //break;
            //}
            //case 2: {
            //p.nj.get().exptype = 1;
            //break;
            //}
            //case 3: {
            //if (p.luong >= 10_000) {

            //synchronized (p.nj){
            //p.nj.maxluggage = 120;
            //}
            //p.upluongMessage(-10_000);
            //} else {
            //p.sendYellowMessage("Ta cũng cần ăn cơm đem 10.000 lượng đến đây ta thông hành trang cho");
            //}
            //break;
            //}
            //default:
            //p.nj.getPlace().chatNPC(p, idNpc, "Ta đứng đây từ " + (util.nextInt(0, 1) == 1 ? "chiều" : "trưa"));
            //}
        } else if (idNpc == 33 && server.manager.EVENT != 0) {
            if (EventItem.entrys.length == 0) {
                return;
            }
            if (index < EventItem.entrys.length) {
                EventItem entry = EventItem.entrys[index];
                if (entry != null) {
                    lamSuKien(p, entry);
                }
            } else {
                String huongDan = "";
                for (EventItem entry : EventItem.entrys) {
                    String s = "";
                    Recipe[] inputs = entry.getInputs();
                    for (int i = 0, inputsLength = inputs.length; i < inputsLength; i++) {
                        Recipe input = inputs[i];
                        val data = input.getItem().getData();
                        s += input.getCount() + " " + data.name;
                        if (inputsLength != inputs.length - 1) {
                            s += ",";
                        }

                    }
                    huongDan += "Để làm " + entry.getOutput().getItem().getData().name + " cần\n\t" + s;
                    if (entry.getCoin() > 0) {
                        huongDan += ", " + entry.getCoin() + " xu";
                    }

                    if (entry.getCoinLock() > 0) {
                        huongDan += ", " + entry.getCoinLock() + " yên";
                    }

                    if (entry.getGold() > 0) {
                        huongDan += ", " + entry.getGold() + " lượng";
                    }
                    huongDan += "\n";

                }

                Service.sendThongBao(p.nj, huongDan);
            }

        } else if (idNpc == 32 && p.nj.getPlace().map.isGtcMap()) {
            if (index == 0) {
                // Tong ket
                Service.sendBattleResult(p.nj, p.nj.getClanBattle());
            } else if (index == 1) {

                // Roi khoi day
                p.nj.changeTypePk(Constants.PK_NORMAL);
                p.nj.getPlace().gotoHaruna(p);
            }
        } else {
            TaskHandle.getTask(p.nj, (byte) idNpc, index, (byte) -1);
        }
        m.cleanup();
    }
// đua top sự kiện tiên nữ
    public static void lamSuKien(User p, EventItem entry) throws IOException {
        boolean enough = true;
        for (Recipe input : entry.getInputs()) {
            int id = input.getId();
            enough = p.nj.enoughItemId(id, input.getCount());
            if (!enough) {
                p.nj.getPlace().chatNPC(p, (short) 33, "Con không đủ " + input.getItemData().name + " để làm sự kiện");
                break;
            }
        }
        if (enough && p.nj.xu >= entry.getCoin() && p.nj.yen >= entry.getCoinLock() && p.luong >= entry.getGold()) {
            for (Recipe input : entry.getInputs()) {
                p.nj.removeItemBags(input.getId(), input.getCount());
            }
            p.nj.addItemBag(true, entry.getOutput().getItem());
            p.nj.upxuMessage(-entry.getCoin());
            p.nj.upyenMessage(-entry.getCoinLock());
            p.upluongMessage(-entry.getGold());
            if (entry.getOutput().getItem().id == 671) {
                p.nj.topSK += 1;
                
            }
        }
    }

    private boolean receiverSingleItem(User p, short idItem, int count) {
        if (!p.nj.checkHanhTrang(count)) {
            p.sendYellowMessage(MSG_HANH_TRANG);
            return true;
        }
        for (int i = 0; i < count; i++) {
            p.nj.addItemBag(false, ItemData.itemDefault(idItem));
        }
        return false;
    }

    private boolean nhanQua(User p, short[] idThuong) {
        if (p.nj.getAvailableBag() == 0) {
            p.sendYellowMessage("Hành trang phải đủ " + idThuong.length + " ô để nhận vật phẩm");
            return true;
        }
        for (short i : idThuong) {
            if (i == 12) {
                val quantity = util.nextInt(100_000_000, 150_000_000);
                p.nj.upyen(quantity);
            } else {
                Item item = ItemData.itemDefault(i);
                p.nj.addItemBag(false, item);
            }
        }
        return false;
    }

    @SneakyThrows
    public static void createMenu(int idNpc, String[] menu, String npcChat, User p) {
        val m = new Message(39);
        m.writer().writeShort(idNpc);
        m.writer().writeUTF(npcChat);
        m.writer().writeByte(menu.length);
        for (String s : menu) {
            m.writer().writeUTF(s);
        }

        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void doMenuArray(final User p, final String[] menu) throws IOException {
        final Message m = new Message(63);
        for (byte i = 0; i < menu.length; ++i) {
            m.writer().writeUTF(menu[i]);
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public void sendWrite(final User p, final short type, final String title) {
        try {
            final Message m = new Message(92);
            m.writer().writeUTF(title);
            m.writer().writeShort(type);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    protected int getJoinAmount10k(final String njname) {

        for (short i = 0; i < this.player10k.size(); ++i) {
            if (this.player10k.get(i).name.equals(njname)) {
                return this.player10k.get(i).joinAmount;
            }
        }
        return 0;
    }

    protected int getJoinAmount20k(final String njname) {

        for (short i = 0; i < this.player20k.size(); ++i) {
            if (this.player20k.get(i).name.equals(njname)) {
                return this.player20k.get(i).joinAmount;
            }
        }
        return 0;
    }

    protected int getJoinAmount50k(final String njname) {

        for (short i = 0; i < this.player50k.size(); ++i) {
            if (this.player50k.get(i).name.equals(njname)) {
                return this.player50k.get(i).joinAmount;
            }
        }
        return 0;
    }

    protected static class Player {

        String user;
        String name;
        int joinAmount;
        String joinXu;
        int id;

        private Player(int id) {
            this.user = null;
            this.name = null;
            this.joinAmount = 0;
            this.joinXu = null;
            this.id = id;
        }
    }
}
