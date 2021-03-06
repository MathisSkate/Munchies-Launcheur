package fr.mathisskate.launcheur.ui;

import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.mathisskate.launcheur.Launcheur;
import fr.mathisskate.launcheur.Main;
import fr.mathisskate.launcheur.utils.Helpers;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import fr.theshark34.swinger.textured.STexturedProgressBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class LauncheurPanel extends JPanel implements SwingerEventListener {

    //Images
    BufferedImage transpa = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("transpa.png")));
    private final STexturedButton dossier = new STexturedButton(transpa);
    BufferedImage imgJouer = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/jouer.png")));
    private final STexturedButton jouer = new STexturedButton(transpa, imgJouer);
    BufferedImage imgLoad = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("load.png")));
    private final STexturedProgressBar progressBar = new STexturedProgressBar(transpa, imgLoad);
    BufferedImage imgOption = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/option.png")));
    private final STexturedButton option = new STexturedButton(transpa, imgOption);
    BufferedImage imgQuit = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/fermer.png")));
    private final STexturedButton quit = new STexturedButton(transpa, imgQuit);
    BufferedImage imgMini = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/mini.png")));
    private final STexturedButton mini = new STexturedButton(transpa, imgMini);
    BufferedImage imgDiscord = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/discord.png")));
    private final STexturedButton discord = new STexturedButton(transpa, imgDiscord);
    BufferedImage imgSite = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("bouton/site.png")));
    private final STexturedButton site = new STexturedButton(transpa, imgSite);
    BufferedImage launcheur = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("launcheur.png")));

    public LauncheurPanel() throws IOException {
        setLayout(null);
        setMinMaxRam(6, 15);

        jouer.setBounds(346, 556, 320, 114);
        jouer.addEventListener(this);
        add(jouer);

        progressBar.setBounds(409, 669, 181, 6);
        progressBar.setStringPainted(true);
        add(progressBar);

        quit.setBounds(638, 358, 50, 50);
        quit.addEventListener(this);
        add(quit);

        mini.setBounds(682, 479, 50, 50);
        mini.addEventListener(this);
        add(mini);

        option.setBounds(660, 525, 50, 50);
        option.addEventListener(this);
        add(option);

        discord.setBounds(290, 570, 50, 50);
        discord.addEventListener(this);
        add(discord);

        site.setBounds(257, 508, 50, 50);
        site.addEventListener(this);
        add(site);

        dossier.setBounds(460, 285, 50, 50);
        dossier.addEventListener(this);
        add(dossier);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(launcheur, 0, 0, this);
    }

    @Override
    public void onEvent(SwingerEvent e) {
        if (e.getSource() == option) {
            Helpers.RAM_SELECTOR.display();
        } else if (e.getSource() == quit) {
            System.exit(0);
        } else if (e.getSource() == mini) {
            Main.frameInstance.setState(Frame.ICONIFIED);
        } else if (e.getSource() == dossier) {
            try {
                Desktop.getDesktop().open(Helpers.MC_DIR.toFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == discord) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI oURL = new URI("https://discord.com/invite/erUg4NnADM");
                desktop.browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == site) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI oURL = new URI("https://munchies.websr.fr");
                desktop.browse(oURL);
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == jouer) {
            Thread t = new Thread(() -> {
                jouer.setEnabled(false);
                try {
                    Launcheur.auth(hasLogged());
                    try {
                        Launcheur.update();
                        if (Helpers.MC_DIR.toFile().exists()) {
                            Helpers.SAVER.set("token", Launcheur.result.getRefreshToken());
                            Helpers.RAM_SELECTOR.save();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        Launcheur.launch();
                    } catch (LaunchException ex) {
                        ex.printStackTrace();
                    }
                } catch (AuthenticationException | MicrosoftAuthenticationException ex) {
                    jouer.setEnabled(true);
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Main.frameInstance,
                            "Impossible de se connecter : Verifie si tu as un compte Minecraft lie Microsoft.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Impossible de mettre a jour ton jeu ! : " + ex.getMessage(),
                            "Erreur !", JOptionPane.ERROR_MESSAGE);
                    jouer.setEnabled(true);
                    ex.printStackTrace();
                }
            });
            t.start();
        }
    }

    public STexturedProgressBar getProgressBar() {
        return progressBar;
    }

    public void setMinMaxRam(int min, int max) {
        int y = 0;
        if (max - min == 9) {
            for (int i = min; i <= max; i++) {
                RamSelector.RAM_ARRAY[y] = i + "Go";
                y++;
            }
        } else
            System.out.println(
                    "Il faut une diff??rence de 9 entre le max est le min, ici la diff??rence est " + (max - min));
    }

    private boolean hasLogged() {
        return Helpers.SAVER.get("token") != null;
    }
}