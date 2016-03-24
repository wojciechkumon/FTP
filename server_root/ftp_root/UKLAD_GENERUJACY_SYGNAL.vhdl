entity uklad_sygnalowy is
	port(CLK : in std_logic;
		 RESET : in std_logic;
		 WYJ : out std_logic);
end uklad_sygnalowy;


architecture arch_uklad_sygnalowy of uklad_sygnalowy is
	signal CYKL std_logic_vector(2 downto 0);

	begin
		process(CLK, RESET)
		begin
			if (RESET = '1') then
				CYKL <= (others => '0');
			elsif (CLK''event and CLK = '1') then
				if ((CYKL = 0) or (CYKL = 4) or (CYKL = 5)) then
					WYJ <= '0';
				else
					WYJ <= '1';
				end if;

				CYKL <= CYKL + 1;
			end if;
		end process;
	end arch_uklad_sygnalowy;