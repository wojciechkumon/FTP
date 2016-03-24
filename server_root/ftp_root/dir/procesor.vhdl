entity proc is
	port(PAM_PROG_ADR : out std_logic_vector(31 downto 0);
		 PAM_PROG_DANE : in std_logic_vector(31 downto 0);
		 PAM_DAN_ADR1 : out std_logic_vector(31 downto 0);
		 PAM_DAN_DANE1 : in std_logic_vector(31 downto 0);
		 PAM_DAN_ADR2 : out std_logic_vector(31 downto 0);
		 PAM_DAN_DANE2 : out std_logic_vector(31 downto 0);
		 CLK : in std_logic;
		 RESET : in std_logic;
		 ZAPIS_PAM : out std_logic;
	);
end proc;


PAM->A (1)
0	ZAPIS REJ ROZKAZOW
1	ZWIEKSZ LICZNIK ROZKAZOW
2	ZAPIS REJ ARGUMENTOW
3 	ZAPIS A, ZWIEKSZ LICZNIK ROZKAZOW

C->PAM (3)
0	ZAPIS REJ ROZKAZOW
1	ZWIEKSZ LICZNIK ROZKAZOW
2	ZAPIS REJ ARGUMENTOW
3 	ZAPIS PAM, ZWIEKSZ LICZNIK ROZKAZOW

SKOK BEZWARUNKOWY
0	ZAPIS REJ ROZKAZOW
1	ZWIEKSZ LICZNIK ROZKAZOW
2	ZAPIS REJ ARGUMENTOW
3 	ZAPIS LICZNIK_ROZKAZOW

	
architecture arch_proc of proc is
	signal CYKL std_logic_vector(1 downto 0);
	signal LICZNIK_ROZKAZOW, REJ_ROZKAZOW, REJ_ARG, A, B, C std_logic_vector(31 downto 0);

	begin
		process(CLK, RESET)
		begin
			if (RESET='1') then
				PAM_PROG_ADR <= (others => '0');
				PAM_DAN_ADR1 <= (others => '0');
				PAM_DAN_ADR2 <= (others => '0');
				PAM_DAN_DANE2 <= (others => '0');
				ZAPIS_PAM <= '0';
				LICZNIK_ROZKAZOW <= (others => '0');
			elsif (CLK''event and CLK='1') then
				CYKL <= CYKL + 1;
				ZAPIS_PAM <= '0';
				if (CYKL = 0) then	
					REJ_ROZKAZOW <= PAM_PROG_DANE;
				else
					if (REJ_ROZKAZOW = 1) then		// PAM->A
						if (CYKL = 1) then
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
						elsif (CYKL = 2) then
							REJ_ARG <= PAM_PROG_DANE;
						else
							A <= PAM_DAN_DANE1;
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
							CYKL <= (others => '0');
						endif;
					elsif (REJ_ROZKAZOW = 2) then 	// PAM->B
						if (CYKL = 1) then
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
						elsif (CYKL = 2) then
							REJ_ARG <= PAM_PROG_DANE;
						else
							B <= PAM_DAN_DANE1;
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
							CYKL <= (others => '0');
						endif;
					elsif (REJ_ROZKAZOW = 3) then	// C->PAM		
						if (CYKL = 1) then
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
						elsif (CYKL = 2) then
							REJ_ARG <= PAM_PROG_DANE;
						elsif (CYKL = 3)
							PAM_DAN_ADR2 <= REJ_ARG;
							PAM_DAN_DANE2 <= C;
						else
							ZAPIS_PAM <= '1';
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
							CYKL <= (others => '0');
						endif;
					elsif (REJ_ROZKAZOW = 4) then	// SKOK BEZWARUNKOWY
						if (CYKL = 1) then
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
						elsif (CYKL = 2) then
							REJ_ARG <= PAM_PROG_DANE;
						else
							LICZNIK_ROZKAZOW <= REJ_ARG;
							CYKL <= (others => '0');
						endif;
					elsif (REJ_ROZKAZOW = 5) then	// SKOK BEZWARUNKOWY
						if (CYKL = 1) then
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
						elsif (CYKL = 2) then
							REJ_ARG <= PAM_PROG_DANE;
						else
							LICZNIK_ROZKAZOW <= REJ_ARG;
							CYKL <= (others => '0');
						endif;
					elsif (REJ_ROZKAZOW = 6) then	// SKOK WARUNKOWY
						if (CYKL = 1) then
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
						elsif (CYKL = 2) then
							REJ_ARG <= PAM_PROG_DANE;
						else
							if (C = 0) then
								LICZNIK_ROZKAZOW <= REJ_ARG
							else 
								LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
							endif;
							CYKL <= (others => '0');
						endif;
					elsif (REJ_ROZKAZOW = 7) then	// OR
						if (CYKL = 1) then
							C <= A or B; // C <= A and B // C <= not A // C <= A + B
							LICZNIK_ROZKAZOW <= LICZNIK_ROZKAZOW + 1;
							CYKL <= (others => '0');
						endif;
					endif;
				endif;
			endif;
		end process;
	end arch_proc;


