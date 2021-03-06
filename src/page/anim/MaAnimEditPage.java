package page.anim;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.CommonStatic;
import common.util.anim.AnimCE;
import common.util.anim.EPart;
import common.util.anim.ImgCut;
import common.util.anim.MaAnim;
import common.util.anim.MaModel;
import common.util.anim.Part;
import common.util.unit.DIYAnim;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.support.AnimLCR;

public class MaAnimEditPage extends Page implements AbEditPage {

	private static final long serialVersionUID = 1L;

	private static final double res = 0.95;

	private static final String[] mod = new String[] { "0 parent", "1 id", "2 sprite", "3 z-order", "4 pos-x",
			"5 pos-y", "6 pivot-x", "7 pivot-y", "8 scale", "9 scale-x", "10 scale-y", "11 angle", "12 opacity",
			"13 horizontal flip", "14 vertical flip", "50 extend" };

	private final JBTN back = new JBTN(0, "back");
	private final JList<DIYAnim> jlu = new JList<>();
	private final JScrollPane jspu = new JScrollPane(jlu);
	private final JList<String> jlt = new JList<>();
	private final JScrollPane jspt = new JScrollPane(jlt);
	private final JList<String> jlp = new JList<>();
	private final JScrollPane jspp = new JScrollPane(jlp);
	private final JList<String> jlm = new JList<>();
	private final JScrollPane jspm = new JScrollPane(jlm);
	private final JList<String> jlv = new JList<>(mod);
	private final JScrollPane jspv = new JScrollPane(jlv);
	private final MaAnimEditTable maet = new MaAnimEditTable(this);
	private final JScrollPane jspma = new JScrollPane(maet);
	private final PartEditTable mpet = new PartEditTable(this);
	private final JScrollPane jspmp = new JScrollPane(mpet);
	private final JTG jtb = new JTG(0, "pause");
	private final JBTN nex = new JBTN(0, "nextf");
	private final JSlider jtl = new JSlider();
	private final SpriteBox sb = new SpriteBox(this);
	private final AnimBox ab = new AnimBox();
	private final JBTN addp = new JBTN(0, "add");
	private final JBTN remp = new JBTN(0, "rem");
	private final JBTN addl = new JBTN(0, "addl");
	private final JBTN reml = new JBTN(0, "reml");
	private final JBTN advs = new JBTN(0, "advs");
	private final JBTN sort = new JBTN(0, "sort");
	private final JLabel inft = new JLabel();
	private final JLabel inff = new JLabel();
	private final JLabel infv = new JLabel();
	private final JLabel infm = new JLabel();
	private final JLabel lmul = new JLabel("</>");
	private final JTF tmul = new JTF();
	private final EditHead aep;

	private Point p = null;
	private boolean pause;

	public MaAnimEditPage(Page p) {
		super(p);

		aep = new EditHead(this, 3);
		ini();
		resized();
	}

	public MaAnimEditPage(Page p, EditHead bar) {
		super(p);

		aep = bar;
		ini();
		resized();
	}

	@Override
	public void callBack(Object o) {
		if (o != null && o instanceof int[])
			change((int[]) o, rs -> {
				if (rs[0] == 0) {
					maet.setRowSelectionInterval(rs[1], rs[2]);
					setC(rs[1]);
				} else {
					mpet.setRowSelectionInterval(rs[1], rs[2]);
					setD(rs[1]);
				}
			});
		int ind = jlt.getSelectedIndex();
		AnimCE ac = maet.anim;
		if (ind < 0 || ac == null)
			return;
		int time = ab.ent == null ? 0 : ab.ent.ind();
		ab.setEntity(ac.getEAnim(ind));
		ab.ent.setTime(time);
	}

	@Override
	public void setSelection(DIYAnim a) {
		change(a, ac -> {
			jlu.setSelectedValue(ac, true);
			setA(ac);
		});
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (p == null)
			return;
		ab.ori.x += p.x - e.getX();
		ab.ori.y += p.y - e.getY();
		p = e.getPoint();
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (!(e.getSource() instanceof AnimBox))
			return;
		p = e.getPoint();
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		p = null;
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (!(e.getSource() instanceof AnimBox))
			return;
		MouseWheelEvent mwe = (MouseWheelEvent) e;
		double d = mwe.getPreciseWheelRotation();
		ab.siz *= Math.pow(res, d);
	}

	@Override
	protected void renew() {

		DIYAnim da = jlu.getSelectedValue();
		int ani = jlt.getSelectedIndex();
		int par = maet.getSelectedRow();
		int row = mpet.getSelectedRow();
		Vector<DIYAnim> vec = new Vector<>();
		if (aep.focus == null)
			vec.addAll(DIYAnim.map.values());
		else
			vec.add(aep.focus);
		change(0, x -> {
			jlu.setListData(vec);
			if (da != null && vec.contains(da)) {
				setA(da);
				jlu.setSelectedValue(da, true);
				if (ani >= 0 && ani < da.anim.anims.length) {
					setB(da, ani);
					if (par >= 0 && par < maet.ma.parts.length) {
						setC(par);
						maet.setRowSelectionInterval(par, par);
						if (row >= 0 && row < mpet.part.moves.length) {
							setD(row);
							mpet.setRowSelectionInterval(row, row);
						}
					}
				}
			} else
				setA(null);
			callBack(null);
		});
	}

	@Override
	protected synchronized void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(aep, x, y, 550, 0, 1750, 50);
		set(back, x, y, 0, 0, 200, 50);

		set(addp, x, y, 300, 750, 200, 50);
		set(remp, x, y, 300, 800, 200, 50);
		set(lmul, x, y, 300, 650, 200, 50);
		set(tmul, x, y, 300, 700, 200, 50);
		set(jspv, x, y, 300, 850, 200, 450);
		set(jspma, x, y, 500, 650, 900, 650);
		set(jspmp, x, y, 1400, 650, 900, 650);
		set(jspu, x, y, 0, 50, 300, 400);
		set(jspt, x, y, 0, 450, 300, 300);
		set(jspm, x, y, 0, 750, 300, 550);
		set(ab, x, y, 300, 50, 700, 500);
		set(jspp, x, y, 1000, 50, 300, 500);
		set(sb, x, y, 1300, 50, 1000, 500);
		set(addl, x, y, 2100, 550, 200, 50);
		set(reml, x, y, 2100, 600, 200, 50);
		set(jtl, x, y, 300, 550, 900, 100);
		set(jtb, x, y, 1200, 550, 200, 50);
		set(nex, x, y, 1200, 600, 200, 50);
		set(inft, x, y, 1400, 550, 250, 50);
		set(inff, x, y, 1650, 550, 250, 50);
		set(infv, x, y, 1400, 600, 250, 50);
		set(infm, x, y, 1650, 600, 250, 50);
		set(advs, x, y, 1900, 550, 200, 50);
		set(sort, x, y, 1900, 600, 200, 50);
		aep.componentResized(x, y);
		maet.setRowHeight(size(x, y, 50));
		mpet.setRowHeight(size(x, y, 50));
		sb.paint(sb.getGraphics());
		ab.paint(ab.getGraphics());
	}

	@Override
	protected void timer(int t) {
		if (!pause)
			eupdate();
		if (ab.ent != null && mpet.part != null) {
			Part p = mpet.part;
			EPart ep = ab.ent.ent[p.ints[0]];
			inft.setText("frame: " + ab.ent.ind());
			inff.setText("part frame: " + (p.frame - p.off));
			infv.setText("actual value: " + ep.getVal(p.ints[1]));
			infm.setText("part value: " + p.vd);
		} else {
			inft.setText("");
			inff.setText("");
			infv.setText("");
			infm.setText("");
		}
		resized();
	}

	private void addListeners() {

		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

		jlu.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (isAdj() || jlu.getValueIsAdjusting())
					return;
				setA(jlu.getSelectedValue());
			}

		});

		jlt.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (isAdj() || jlt.getValueIsAdjusting())
					return;
				DIYAnim da = jlu.getSelectedValue();
				int ind = jlt.getSelectedIndex();
				setB(da, ind);
			}

		});

		jlp.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (isAdj() || jlp.getValueIsAdjusting())
					return;
				sb.sele = jlp.getSelectedIndex();
			}

		});

		jlm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (isAdj() || jlm.getValueIsAdjusting() || maet.ma == null)
					return;
				int ind = jlm.getSelectedIndex();
				for (int i = 0; i < maet.ma.n; i++)
					if (maet.ma.parts[i].ints[0] == ind) {
						setC(i);
						return;
					}
				setC(-1);
			}

		});

	}

	private void addListeners$1() {
		ListSelectionModel lsm = maet.getSelectionModel();

		lsm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (isAdj() || lsm.getValueIsAdjusting())
					return;
				int ind = maet.getSelectedRow();
				change(ind, i -> setC(i));
			}

		});

		addp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				change(0, x -> {
					int ind = maet.getSelectedRow() + 1;
					MaAnim ma = maet.ma;
					Part[] data = ma.parts;
					ma.parts = new Part[++ma.n];
					for (int i = 0; i < ind; i++)
						ma.parts[i] = data[i];
					for (int i = ind; i < data.length; i++)
						ma.parts[i + 1] = data[i];
					Part np = new Part();
					np.validate();
					ma.parts[ind] = np;
					ma.validate();
					maet.anim.unSave("maanim add part");
					callBack(null);
					resized();
					lsm.setSelectionInterval(ind, ind);
					setC(ind);
					int h = mpet.getRowHeight();
					mpet.scrollRectToVisible(new Rectangle(0, h * ind, 1, h));
				});
			}

		});

		remp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				change(0, x -> {
					MaAnim ma = maet.ma;
					int[] rows = maet.getSelectedRows();
					Part[] data = ma.parts;
					for (int row : rows)
						data[row] = null;
					ma.n -= rows.length;
					ma.parts = new Part[ma.n];
					int ind = 0;
					for (int i = 0; i < data.length; i++)
						if (data[i] != null)
							ma.parts[ind++] = data[i];
					ind = rows[rows.length - 1];
					ma.validate();
					maet.anim.unSave("maanim remove part");
					callBack(null);
					if (ind >= ma.n)
						ind = ma.n - 1;
					lsm.setSelectionInterval(ind, ind);
					setC(ind);
				});
			}

		});

		tmul.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				double d = CommonStatic.parseIntN(tmul.getText()) * 0.01;
				if (!Opts.conf("times animation length by " + d))
					return;
				for (Part p : maet.ma.parts) {
					for (int[] line : p.moves)
						line[0] *= d;
					p.off *= d;
					p.validate();
				}
				maet.ma.validate();
				maet.anim.unSave("maanim multiply");
			}

		});

	}

	private void addListeners$2() {
		ListSelectionModel lsm = mpet.getSelectionModel();

		lsm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (isAdj() || lsm.getValueIsAdjusting())
					return;
				setD(lsm.getLeadSelectionIndex());
			}

		});

		addl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Part p = mpet.part;
				int[][] data = p.moves;
				p.moves = new int[++p.n][];
				for (int i = 0; i < data.length; i++)
					p.moves[i] = data[i];
				p.moves[p.n - 1] = new int[4];
				p.validate();
				maet.ma.validate();
				callBack(null);
				maet.anim.unSave("maanim add line");
				resized();
				change(p.n - 1, i -> lsm.setSelectionInterval(i, i));
				setD(p.n - 1);
				int h = mpet.getRowHeight();
				mpet.scrollRectToVisible(new Rectangle(0, h * (p.n - 1), 1, h));
			}

		});

		reml.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] inds = mpet.getSelectedRows();
				if (inds.length == 0)
					return;
				Part p = mpet.part;
				List<int[]> l = new ArrayList<>();
				int j = 0;
				for (int i = 0; i < p.n; i++)
					if (j >= inds.length || i != inds[j])
						l.add(p.moves[i]);
					else
						j++;
				p.moves = l.toArray(new int[0][]);
				p.n = l.size();
				p.validate();
				maet.ma.validate();
				callBack(null);
				maet.anim.unSave("maanim remove line");
				int ind = inds[0];
				if (ind >= p.n)
					ind--;
				change(ind, i -> lsm.setSelectionInterval(i, i));
				setD(ind);
			}

		});
	}

	private void addListeners$3() {

		jtb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pause = jtb.isSelected();
				jtl.setEnabled(pause && ab.ent != null);
			}
		});

		nex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				eupdate();
			}
		});

		jtl.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (isAdj() || !pause)
					return;
				ab.ent.setTime(jtl.getValue());
			}
		});

		advs.setLnr(x -> changePanel(new AdvAnimEditPage(this, maet.anim, jlt.getSelectedIndex())));

		sort.setLnr(x -> Arrays.sort(maet.ma.parts));

	}

	private void eupdate() {
		ab.update();
		if (ab.ent != null)
			change(0, x -> jtl.setValue(ab.ent.ind()));
	}

	private void ini() {
		add(aep);
		add(back);
		add(jspu);
		add(jspp);
		add(jspt);
		add(jspm);
		add(jspv);
		add(jspma);
		add(jspmp);
		add(addp);
		add(remp);
		add(addl);
		add(reml);
		add(jtb);
		add(jtl);
		add(nex);
		add(sb);
		add(ab);
		add(inft);
		add(inff);
		add(infv);
		add(infm);
		add(lmul);
		add(tmul);
		add(advs);
		add(sort);
		jlu.setCellRenderer(new AnimLCR());
		inft.setBorder(BorderFactory.createEtchedBorder());
		inff.setBorder(BorderFactory.createEtchedBorder());
		infv.setBorder(BorderFactory.createEtchedBorder());
		infm.setBorder(BorderFactory.createEtchedBorder());
		lmul.setBorder(BorderFactory.createEtchedBorder());
		addp.setEnabled(false);
		remp.setEnabled(false);
		addl.setEnabled(false);
		reml.setEnabled(false);
		jtl.setEnabled(false);
		jtl.setPaintTicks(true);
		jtl.setPaintLabels(true);
		addListeners();
		addListeners$1();
		addListeners$2();
		addListeners$3();
	}

	private void setA(DIYAnim dan) {
		if (dan != null && dan.getAnimC().mismatch)
			dan = null;
		change(dan, da -> {
			aep.setAnim(da);
			if (da == null) {
				jlt.setListData(new String[0]);
				sb.setAnim(null);
				jlp.setListData(new String[0]);
				setB(null, -1);
				return;
			}
			AnimCE anim = da.getAnimC();
			int ind = jlt.getSelectedIndex();
			String[] val = anim.names();
			jlt.setListData(val);
			if (ind >= val.length)
				ind = val.length - 1;
			jlt.setSelectedIndex(ind);
			setB(da, ind);
			sb.setAnim(anim);
			ImgCut ic = anim.imgcut;
			String[] name = new String[ic.n];
			for (int i = 0; i < ic.n; i++)
				name[i] = i + " " + ic.strs[i];
			jlp.setListData(name);
			MaModel mm = anim.mamodel;
			name = new String[mm.n];
			for (int i = 0; i < mm.n; i++)
				name[i] = i + " " + mm.strs0[i];
			jlm.setListData(name);
		});
	}

	private void setB(DIYAnim da, int ind) {
		change(0, x -> {
			AnimCE ac = da == null ? null : da.getAnimC();
			MaAnim anim = ac == null || ind < 0 ? null : ac.anims[ind];
			addp.setEnabled(anim != null);
			tmul.setEditable(anim != null);
			advs.setEnabled(anim != null);
			sort.setEnabled(anim != null);
			jtl.setEnabled(anim != null);
			if (da == null || ind == -1) {
				maet.setAnim(null, null);
				ab.setEntity(null);
				setC(-1);
				return;
			}
			int row = maet.getSelectedRow();
			maet.setAnim(ac, anim);
			ab.setEntity(ac.getEAnim(ind));
			if (row >= maet.getRowCount()) {
				maet.clearSelection();
				row = -1;
			}
			setC(row);

			jtl.setMinimum(0);
			jtl.setMaximum(ab.ent.len());
			jtl.setLabelTable(null);
			if (ab.ent.len() <= 50) {
				jtl.setMajorTickSpacing(5);
				jtl.setMinorTickSpacing(1);
			} else if (ab.ent.len() <= 200) {
				jtl.setMajorTickSpacing(10);
				jtl.setMinorTickSpacing(2);
			} else if (ab.ent.len() <= 1000) {
				jtl.setMajorTickSpacing(50);
				jtl.setMinorTickSpacing(10);
			} else if (ab.ent.len() <= 5000) {
				jtl.setMajorTickSpacing(250);
				jtl.setMinorTickSpacing(50);
			} else {
				jtl.setMajorTickSpacing(1000);
				jtl.setMinorTickSpacing(200);
			}
		});
	}

	private void setC(int ind) {
		remp.setEnabled(ind >= 0);
		addl.setEnabled(ind >= 0);
		Part p = ind < 0 || ind >= maet.ma.parts.length ? null : maet.ma.parts[ind];
		change(0, x -> {
			mpet.setAnim(maet.anim, maet.ma, p);
			mpet.clearSelection();
			ab.setSele(p == null ? -1 : p.ints[0]);

			if (ind >= 0) {
				int par = p.ints[0];
				jlm.setSelectedIndex(par);
				jlv.setSelectedIndex(mpet.part.ints[1]);
				if (maet.getSelectedRow() != ind) {
					maet.setRowSelectionInterval(ind, ind);
					maet.scrollRectToVisible(maet.getCellRect(ind, 0, true));
				}
				ab.setSele(par);
				int ic = mpet.anim.mamodel.parts[par][2];
				jlp.setSelectedIndex(ic);
				Rectangle r = jlp.getCellBounds(ic, ic);
				if (r != null)
					jlp.scrollRectToVisible(r);
				sb.sele = jlp.getSelectedIndex();
			} else
				maet.clearSelection();
		});
		setD(-1);
	}

	private void setD(int ind) {
		reml.setEnabled(ind >= 0);
		if (ind >= 0 && mpet.part.ints[1] == 2) {
			change(mpet.part.moves[ind][1], i -> jlp.setSelectedIndex(i));
			sb.sele = jlp.getSelectedIndex();
		}
	}

}
