package br.senai.sp.cotia.jogodavelhaapp.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

import br.senai.sp.cotia.jogodavelhaapp.R;
import br.senai.sp.cotia.jogodavelhaapp.databinding.FragmentJogoBinding;
import br.senai.sp.cotia.jogodavelhaapp.util.PrefsUtil;

public class FragmentJogo extends Fragment {
    // variável para acessar os elementos na view
    private FragmentJogoBinding binding;
    //vetor para agrupar os botões
    private Button[] botoes;
    //variável que representa o tabuleiro
    private String[][] tabuleiro;
    //variável para os símbolos X / O
    private String simbJog1, simbJog2, simbolo;
    //variável Random para sortear quem começa
    private Random random;
    //variável para contar o número de jogadas
    private int numJogadas =0;
    //variáveis para o placar
    private int placarJog1 = 0, placarJog2 = 0;
    // variável para o placar do jogo da velha
    private int placarVelha = 0;
    // variável para o alerta do resetar
    private AlertDialog alerta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Alerta reset");
        alert.setMessage("Deseja mesmo resetar?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Resetando..", Toast.LENGTH_SHORT).show();
                placarJog1 = 0;
                placarJog2 = 0;
                placarVelha = 0;
                resetar();
                atualizarPlacar();
            }
        });
        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Sem resetar!", Toast.LENGTH_SHORT).show();
            }
        });
        alerta = alert.create();

        //habilita o menu neste fragment
        setHasOptionsMenu(true);

        //instancia o binding
        binding = FragmentJogoBinding.inflate(inflater, container, false);

        //instancia o vetor
        botoes = new Button[9];
        //agrupa os botões no vetor
        botoes[0] = binding.bt00;
        botoes[1] = binding.bt01;
        botoes[2] = binding.bt02;
        botoes[3] = binding.bt10;
        botoes[4] = binding.bt11;
        botoes[5] = binding.bt12;
        botoes[6] = binding.bt20;
        botoes[7] = binding.bt21;
        botoes[8] = binding.bt22;

        //associa o listener aos botões
        for (Button bt : botoes){
            bt.setOnClickListener(listenerBotoes);
        }

        //instancia o tabuleiro
        tabuleiro = new String[3][3];

        //preencher o tabuleiro com ""
        for (String[] vetor : tabuleiro){
            Arrays.fill(vetor, "");
        }

        /* Faz a mesma coisa que acima
        for (i = 0; i < 3; i++){
            for (j = 0; j < 3; j++){
                tabuleiro[i][j] = "";
            }
        }
         */

        //instancia o Random
        random = new Random();

        //define os símbolos dos jogadores
        simbJog1 = PrefsUtil.getSimboloJog1(getContext());
        simbJog2 = PrefsUtil.getSimboloJog2(getContext());

        //altera o símbolo do jogador no placar
        binding.tvJogador1.setText(getResources().getString(R.string.jogadorO, simbJog1));
        binding.tvJogador2.setText(getResources().getString(R.string.jogadorX, simbJog2));

        //sorteia quem inicia o jogo
        sorteia();

        //atualiza a vez
        atualizaVez();

        //retorna a view do fragment
        return  binding.getRoot();
    }

    private void sorteia(){
        //caso o random gere um valor verdadeiro, jogador 1 começa
        //caso contrário o jogador 2 começa
        if(random.nextBoolean()){
            simbolo = simbJog1;
        }else{
            simbolo = simbJog2;
        }

    }

    private void atualizaVez(){
        //verifica de quem é a vez e pinta o placar do jogador em questão
        if (simbolo.equals(simbJog1)){
            binding.linearJog1.setBackgroundColor(getResources().getColor(R.color.white));
            binding.linearJog2.setBackgroundColor(getResources().getColor(R.color.purple_200));
        }else{
            binding.linearJog2.setBackgroundColor(getResources().getColor(R.color.white));
            binding.linearJog1.setBackgroundColor(getResources().getColor(R.color.purple_200));
        }
    }

    private void resetar(){
        //zerar o tabuleiro
        for (String[] vetor : tabuleiro){
            Arrays.fill(vetor, "");

        }
        //percorre o vetor de botões resetando-os
        for (Button botao : botoes){
            botao.setBackgroundColor(getResources().getColor(R.color.pink_800));
            botao.setText("");
            botao.setClickable(true);

        }
        //sorteia quem irá iniciar o próximo jogo
        sorteia();
        //atualiza vez no placar
        atualizaVez();
        //zerar o número de jogadas
        numJogadas = 0;

    }

    private boolean venceu(){
        //verifica se venceu nas linhas
        for (int i = 0; i < 3; i++){
            if (tabuleiro[i][0].equals(simbolo) && tabuleiro[i][1].equals(simbolo) && tabuleiro[i][2].equals(simbolo)){
                return true;
            }
        }

        /*Verifica se venceu nas linhas
        for (int l = 0; l < 3; l++){
            boolean v = true;
            for (int c = 0; c < tabuleiro[l].length; c++){
                if (!tabuleiro[l][c].equals(simbolo)){
                    v = false;
                    break;
                }
            }
        }*/

        //verifica se venceu na coluna
        for (int i = 0; i < 3; i++){
            if (tabuleiro[0][i].equals(simbolo) && tabuleiro[1][i].equals(simbolo) && tabuleiro[2][i].equals(simbolo)){
                return true;
            }
        }

        //verifica se venceu nas diagonais
        if (tabuleiro[0][0].equals(simbolo) && tabuleiro[1][1].equals(simbolo) && tabuleiro[2][2].equals(simbolo)){
            return true;
        }

        if (tabuleiro[0][2].equals(simbolo) && tabuleiro[1][1].equals(simbolo) && tabuleiro[2][0].equals(simbolo)){
            return true;
        }

        return false;
    }



    private void atualizarPlacar(){
        binding.placar.setText(placarJog1+"");
        binding.placar2.setText(placarJog2+"");
        binding.placarVelha.setText(placarVelha+"");
    }

    //método que cria e "infla" o menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //começa um novo jogo do zero
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //verifica qual botão foi clicado no menu
        switch (item.getItemId()){
            //caso tenha clicado no resetar
            case R.id.menu_resetar:
                alerta.show();
                break;
                //caso tenha clicado no referências
            case R.id.menu_prefs:
                NavHostFragment.findNavController(FragmentJogo.this).navigate(R.id.action_fragmentJogo_to_prefFragment);
                break;

            case R.id.menu_inicio:
                NavHostFragment.findNavController(FragmentJogo.this).navigate(R.id.action_fragmentJogo_to_fragmentInicio);
                break;
        }
        return true;
    }

    private View.OnClickListener listenerBotoes = btPress -> {
        //incrementa as jogadas
        numJogadas++;
        // pega o nome do botão
        String nomeBotao = getContext().getResources().getResourceName(btPress.getId());
        // extrai os 2 últimos caracteres do nomeBotao
        String posicao = nomeBotao.substring(nomeBotao.length()-2);
        // extrai a posição em linha e em coluna
        int linha = Character.getNumericValue(posicao.charAt(0));
        int coluna = Character.getNumericValue(posicao.charAt(1));
        // marca no tabuleiro o símbolo que foi jogado
        tabuleiro[linha][coluna] = simbolo;
        // a variável btPress representa o botão que foi clicado
        // faz um casting de view prs button
        Button botao = (Button) btPress;
        // trocar o texto do botão que foi clicado
        botao.setText(simbolo);
        //desabilitar o botão
        botao.setClickable(false);
        //troca o bacjground do botão
        botao.setBackgroundColor(Color.rgb(255, 153, 153));
        //verifica se venceu
        if (numJogadas >= 5 && venceu()){
            //exibe em Toast informando que o jogador venceu
            Toast.makeText(getContext(),R.string.venceu, Toast.LENGTH_SHORT).show();
            //verifica quem venceu e atualiza o placar
            if (simbolo.equals(simbJog1)){
                placarJog1++;
            }else if (simbolo.equals(simbJog2)){
                placarJog2++;
            }else{
                placarVelha++;
            }
            //atualiza o placar
            atualizarPlacar();
            //resetar o tabuleiro
            resetar();

        }else if(numJogadas == 9) {
            //exibe em Toast informando que deu velha
            Toast.makeText(getContext(),R.string.deuvelha, Toast.LENGTH_SHORT).show();
            placarVelha++;
            atualizarPlacar();
            //resetar o tabuleiro
            resetar();
        }else{
            // inverter a vez
            //método com if
        /*
        if (simbolo.equals(simbJog1)){
            simbolo = simbJog1;
        }else{
            simbolo = simbJog2;
        }
        */
            //método com operador ternário
            // inverter a vez
            simbolo = simbolo.equals(simbJog1) ? simbJog2 : simbJog1;
            //atualiza a vez
            atualizaVez();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // para "sumir" com a toolbar
        // pegar uma referência do tipo AppCompatActivity
        AppCompatActivity minhaActivity = (AppCompatActivity) getActivity();
        // oculta a actionbar
        minhaActivity.getSupportActionBar().show();
        // retira o botão de voltar
        minhaActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}